package com.seojin.batch.biz.sample.chunk;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.seojin.batch.sys.base.BaseAbstractJob;

/**
 * Description : Chunk sample
 * <p>
 *
 * <pre>
 * Note
 * - writer에서 2개 이상의 Table에 data를 적용해야 할 경우
 * - CompositeItemWriter를 통한 위임 패턴구현
 * 
 * - SampleChunkJob1Config와 같은 기능을 수행하지만 아래 조건을 만족해야 한다.
 * 	 - 10건 단위로 commit 처리가 되어야 한다.
 *   - Use_Yn, rgst_dtm 필드를 Update한다.
 *     (보통은 writer에서 한번에 Update 하겠지만, 테스트를 위해 2개의 별도 테이블에 Update하는 것 처럼 2번에 걸쳐 처리.)  
 * </pre>
 */
@Configuration
public class SampleChunkJob3Config extends BaseAbstractJob {

	/**
	 * Job Name
	 */
	private static final String JOB_NAME = "sample-chunk-job3";

	/**
	 * Chunk Size
	 * 10건 단위로 commit하기 위해 Chunk Size를 10으로 지정한다. 
	 */
	private static final int CHUNK_SIZE = 10;

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
	 * Description : Job Bean 생성
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
	 * Description : Step Bean 생성
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_step")
	public Step batchJobStep() {
		return stepBuilder.get(JOB_NAME + "_step")
				.<SampleChunkJob1, SampleChunkJob1> chunk(CHUNK_SIZE)
				.reader(myBatisCursorItemReader())
				.processor(itemProcessor())
				//compositeItemWriter 지정
				.writer(compositeItemWriter())
				.build();
	}

	/**
	 * Description : Reader Bean 생성
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
	 * Description : Processor Bean 생성
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

			return item;
		};
	}

	/**
	 * Description : Writer Bean 생성
	 * - 위임 패턴중 하나인 compositeItemWriter build 사용
	 * - 지정된 순서대로 순차적으로 실행 
	 * <p> 
	 * @return
	 */
	@Bean(name = JOB_NAME + "_writer")
	public CompositeItemWriter<SampleChunkJob1> compositeItemWriter() {
		return new CompositeItemWriterBuilder<SampleChunkJob1>()
				.delegates(myBatisBatchItemWriter1(), myBatisBatchItemWriter2())
				.build();
	}

	/**
	 * Description : Writer Bean 생성
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_writer1")
	public MyBatisBatchItemWriter<SampleChunkJob1> myBatisBatchItemWriter1() {
		return new MyBatisBatchItemWriterBuilder<SampleChunkJob1>()
				.sqlSessionFactory(sqlSessionFactory)
				.statementId("com.seojin.batch.biz.sample.chunk.SampleChunkJob1Mapper.updateBatchSample")
				.build();
	}

	/**
	 * Description : Writer Bean 생성
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_writer2")
	public MyBatisBatchItemWriter<SampleChunkJob1> myBatisBatchItemWriter2() {
		return new MyBatisBatchItemWriterBuilder<SampleChunkJob1>()
				.sqlSessionFactory(sqlSessionFactory)
				.statementId("com.seojin.batch.biz.sample.chunk.SampleChunkJob1Mapper.updateBatchSampleRgstDtm")
				.build();
	}
}
