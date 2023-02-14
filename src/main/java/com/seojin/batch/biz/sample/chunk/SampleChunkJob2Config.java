package com.seojin.batch.biz.sample.chunk;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.seojin.batch.sys.base.BaseAbstractJob;

/**
 * Description : Chunk size sample
 * <p>
 *
 * <pre>
 * Note
 * - faultTolerant, (skipLimit 적용)
 * 	   : skipLimit이외에도 retryLimit, noRollback등이 있다.
 * 
 * - SampleChunkJob1Config와 같은 기능을 수행하지만 아래 조건을 만족해야 한다.
 * 	 - 1건 단위로 commit 처리가 되어야 하며,
 *   - 3번 이상 오류가 발생하면 실패 처리한다.
 * </pre>
 */
@Configuration
public class SampleChunkJob2Config extends BaseAbstractJob {

	/**
	 * Job Name
	 */
	private static final String JOB_NAME = "sample-chunk-job2";

	/**
	 * Chunk Size
	 * 1건 단위로 commit하기 위해 Chunk Size를 1로 지정한다. 
	 */
	private static final int CHUNK_SIZE = 1;

	/**
	 * 오류발생을 위한 순번 지정
	 */
	private static final int SORT_SEQ = 2;

	/**
	 * Session Factory
	 */
	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Override
	protected String jobName() {
		return JOB_NAME;
	}

	/**
	 * Description : Job Bean을 생성한다.
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME)
	public Job batchJob() {
		return getJobBuilder()
				.start(batchJobStep())
				.build();
	}

	/**
	 * Description : Job에서 사용할 Step Bean을 생성한다.
	 * - 예외 조건에 해당하는 옵션을 추가한다.
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_step")
	public Step batchJobStep() {
		return stepBuilder.get(JOB_NAME + "_step")
				.<SampleChunkJob1, SampleChunkJob1> chunk(CHUNK_SIZE)
				.reader(myBatisCursorItemReader())
				.processor(itemProcessor())
				.writer(myBatisBatchItemWriter())
				.faultTolerant() //오류 허용을 위한 설정을 추가한다.
				.skipLimit(2) //허용할 오류 횟수를 지정한다.
				.skip(Exception.class) //오류로 처리할 대상 Exception을 지정한다.
				.retryLimit(2)
				.retry(Exception.class)
				.build();
	}

	/**
	 * Description : Job에서 사용할 Reader Bean을 생성한다.
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_reader")
	public MyBatisCursorItemReader<SampleChunkJob1> myBatisCursorItemReader() {
		return new MyBatisCursorItemReaderBuilder<SampleChunkJob1>()
				.sqlSessionFactory(sqlSessionFactory)
				.queryId("com.seojin.batch.biz.sample.chunk.SampleChunkJob1Mapper.selectBatchSample")
				.build();
	}

	/**
	 * Description : Job에서 사용할 Processor Bean을 생성한다.
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_processor")
	public ItemProcessor<SampleChunkJob1, SampleChunkJob1> itemProcessor() {
		return item -> {
			if ("Y".equals(item.getUseYn())) {
				item.setUseYn("N");
			} else {
				item.setUseYn("Y");
			}

			//오류발생을 위해 임의로 null 처리
			if (item.getSortSeq() >= SORT_SEQ) {
				item.setUseYn(null);
			}

			return item;
		};
	}

	/**
	 * Description : Job에서 사용할 Writer Bean을 생성한다.
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_writer")
	public MyBatisBatchItemWriter<SampleChunkJob1> myBatisBatchItemWriter() {
		return new MyBatisBatchItemWriterBuilder<SampleChunkJob1>()
				.sqlSessionFactory(sqlSessionFactory)
				.statementId("com.seojin.batch.biz.sample.chunk.SampleChunkJob1Mapper.updateBatchSample")
				.build();
	}
}
