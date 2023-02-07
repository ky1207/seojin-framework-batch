package com.seojin.batch.biz.sample.tasklet;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.seojin.batch.sys.base.BaseAbstractJob;

import lombok.extern.slf4j.Slf4j;

/**
 * Description : parameters Test
 * 0 0/1 * * * ?
 * <p>
 */
@Configuration
@Slf4j
public class SampleTaskletJob3Config extends BaseAbstractJob {

	/**
	 * Job Name 지정 : Bean등록, JobLaunch에서 호출시 사용되며, 중복되지 않도록 한다.
	 */
	private static final String JOB_NAME = "sample-tasklet-job3";

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

			StepContext stepContext = chunkContext.getStepContext();
			Map<String, Object> jobParameters = stepContext.getJobParameters();

			for (String strKey : jobParameters.keySet()) {
				String strValue = jobParameters.get(strKey).toString();

				log.debug("sample-tasklet-job3 ||| key:{}, value:{}", strKey, strValue);

			}

			//작업완료 상태코드를 Return한다.
			return RepeatStatus.FINISHED;

		}).build();
	}
}