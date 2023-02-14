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
 * Description : Basic Chunk
 * <p>
 *
 * <pre>
 * Note
 * - SampleTaskletJob1Config와 동일한 기능을 chunk방식으로 구현한다.
 * - Chunk 방식은 Chunk Size 단위로 Transaction이 처리되기에 CHUNK_SIZE를 100 (BATCH_SAMPLE Data : 95건)으로 지정하여 처리했다.
 * </pre>
 *
 */
@Configuration
public class SampleChunkJob1Config extends BaseAbstractJob {

	/**
	 * Job Name 지정
	 * JobLaunch에서 호출되며, 모든 JOB, STEP은 Bean으로 등록된다.
	 */
	private static final String JOB_NAME = "sample-chunk-job1";

	/**
	 * Chunk Size 지정
	 * 지정된 Size 단위로 Transaction 처리가 된다.
	 * Tasklet와 동일하게 처리하기 위해 10으로 설정했다.
	 */
	private static final int CHUNK_SIZE = 100;

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
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_step")
	public Step batchJobStep() {
		return stepBuilder.get(JOB_NAME + "_step")
				.<SampleChunkJob1, SampleChunkJob1> chunk(CHUNK_SIZE)
				.reader(myBatisCursorItemReader())
				// 업무에 따라 processor는 생략가능하다.
				.processor(itemProcessor())
				.writer(myBatisBatchItemWriter())
				.build();
	}

	/**
	 * Description : Step에서 사용할 reader Bean을 생성한다.
	 * - 1건씩 Read 후 processor로 전달한다.
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
	 * Description : Step에서 사용할 processor Bean을 생성한다.
	 * - 1건씩 처리하며, 지정된 Chunk Size이상이 된 경우 Write로 전달한다.
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
	 * Description : Step에서 사용할 writer Bean을 생성한다.
	 * - 지정된 chunk size 단위로 Transaction 처리한다.
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
