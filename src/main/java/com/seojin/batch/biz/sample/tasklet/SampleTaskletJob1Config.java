package com.seojin.batch.biz.sample.tasklet;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.seojin.batch.sys.base.BaseAbstractJob;

/**
 * Description : SampleChunkJob1Config와 동일한 기능을 Tasklet방식으로 구현
 * <p>
 *
 * <pre>
 * Note
 * ※ tasklet 방식 
 * - Step별 한개의 Transaction으로 처리된다.
 * - 비용이 많이 들어가기에 단순한 쿼리 수행 시에만 사용하고, 가능하면 Chunk방식을 사용한다. 
 *
 * </pre>
 */
@Configuration
public class SampleTaskletJob1Config extends BaseAbstractJob {

	/**
	 * Job Name 지정 : Bean등록, JobLaunch에서 호출시 사용되며, 중복되지 않도록 한다.
	 */
	private static final String JOB_NAME = "sample-tasklet-job1";

	/**
	 * SampleTaskletJob1Mapper
	 */
	@Autowired
	private SampleTaskletJob1Mapper sampleTaskletJob1Mapper;

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

		return stepBuilder.get(JOB_NAME + "_step").tasklet((stepContribution, chunkContext) -> {

			// Data를 검색한다.
			// chunk 방식에서 reader와 같은 역활
			List<SampleTaskletJob1> batchSampleList = sampleTaskletJob1Mapper.selectBatchSample();

			// Spring Batch Table에 read 건수를 등록하기 위해 추가한다.
			// chunk 방식은 자동으로 처리된다. 
			stepContribution.incrementReadCount();

			// 검색한 Data를 가공 후 Write한다.
			// chunk 방식에서 processor, writer 와 같은 역활
			for (SampleTaskletJob1 sampleTaskletJob1 : batchSampleList) {

				if ("Y".equals(sampleTaskletJob1.getUseYn())) {
					sampleTaskletJob1.setUseYn("N");
				} else {
					sampleTaskletJob1.setUseYn("Y");
				}

				int uCnt = sampleTaskletJob1Mapper.updateBatchSample(sampleTaskletJob1);

				// Spring Batch Table에 write 건수를 등록하기 위해 추가한다.
				// chunk 방식은 자동으로 처리된다. 
				stepContribution.incrementWriteCount(uCnt);
			}

			//작업완료 상태코드를 Return한다.
			return RepeatStatus.FINISHED;

		}).build();
	}
}