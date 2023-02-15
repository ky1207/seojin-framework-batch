package com.seojin.batch.biz.sample.chunk;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.seojin.batch.sys.base.BaseAbstractJob;

/**
 * Description : step 간 Data 공유
 * <p>
 *
 * <pre>
 * Note
 *   주의)
 *   공유되는 데이터는 작아야 한다.
 *   문자열이나 단순한 값을 공유하는 데 사용하며, 컬렉션이나 막대한 양의 데이터를 공유하지 않는다.
 * - filtering records 
 * 
 * - RGST_ID 중 Max인 값을 검색 후 SORT_SEQ가 15~25번인 Record 만 Update한다. 
 * </pre>
 */
@Configuration
public class SampleChunkJob4Config extends BaseAbstractJob {

	/**
	 * Job Name
	 */
	private static final String JOB_NAME = "sample-chunk-job4";

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
	 * SqlSessionTemplate
	 */
	public SqlSessionTemplate sqlSessionTemplate() {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	/**
	 * Description : Step간 Data 공유를 위한 Listener를 생성한다.
	 * - Data를 넣을 key값을 생성시 지정해야 한다.
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_listener")
	public StepExecutionListener promotionListener() {
		ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
		listener.setKeys(new String[] { "RGST_ID" });
		return listener;
	}

	/**
	 * Description : Job Bean 생성
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME)
	public Job batchJob() {
		return getJobBuilder()
				.start(excutionContextStep1()) //RGST_ID 조회를 위한 Step 지정
				.next(batchJobStep()) //RGST_ID Update를 위한 Step 지정			
				.build();
	}

	/**
	 * Description : Step Bean 생성
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_step1")
	public Step excutionContextStep1() {
		return stepBuilder.get(JOB_NAME + "_step1").tasklet((stepContribution, chunkContext) -> {

			String sRgstId = sqlSessionTemplate().selectOne("com.seojin.batch.biz.sample.chunk.SampleChunkJob1Mapper.selectBatchSampleMax");

			//검색한 RGST_ID를 listener에 저장한다.
			ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
			jobContext.put("RGSTER_NO", sRgstId);

			return RepeatStatus.FINISHED;
		})
				//Data 공유를 위한 Listener를 지정한다.
				.listener(promotionListener())
				.build();
	}

	/**
	 * Description : Step Bean 생성
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_step2")
	public Step batchJobStep() {
		return stepBuilder.get(JOB_NAME + "_step2")
				.<SampleChunkJob1, SampleChunkJob1> chunk(CHUNK_SIZE)
				.reader(myBatisCursorItemReader())
				.processor(itemProcessor())
				.writer(myBatisBatchItemWriter())
				//Data 공유를 위한 Listener를 지정한다.
				.listener(promotionListener())
				.build();
	}

	/**
	 * Description : Reader Bean 생성
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_reader2")
	public MyBatisCursorItemReader<SampleChunkJob1> myBatisCursorItemReader() {
		return new MyBatisCursorItemReaderBuilder<SampleChunkJob1>()
				.sqlSessionFactory(sqlSessionFactory)
				.queryId("com.seojin.batch.biz.sample.chunk.SampleChunkJob1Mapper.selectBatchSample")
				.build();
	}

	/**
	 * Description : Processor Bean 생성
	 * - filtering이 필요한 경우 null을 return한다.  
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_processor2")
	public ItemProcessor<SampleChunkJob1, SampleChunkJob1> itemProcessor() {
		return item -> {

			//Sort_Seq 조건 점검
			if (item.getSortSeq() >= 15 && item.getSortSeq() <= 25) {
				//Listener에 저장된 data 추출  
				String sRgsterNo = (String) JobSynchronizationManager.getContext().getJobExecution().getExecutionContext().get("RGSTER_NO");

				item.setRgsterNo(sRgsterNo);

			} else {
				return null;
			}

			return item;
		};
	}

	/**
	 * Description : Writer Bean 생성
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_writer2")
	public MyBatisBatchItemWriter<SampleChunkJob1> myBatisBatchItemWriter() {
		return new MyBatisBatchItemWriterBuilder<SampleChunkJob1>()
				.sqlSessionFactory(sqlSessionFactory)
				.statementId("com.seojin.batch.biz.sample.chunk.SampleChunkJob1Mapper.updateBatchSampleRgstDtm")
				.build();
	}

}
