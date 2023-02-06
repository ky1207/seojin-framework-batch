package com.seojin.batch.biz.sample.tasklet;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.seojin.batch.sys.base.BaseAbstractJob;

import lombok.extern.slf4j.Slf4j;

/**
 * Description : Step flow Sample
 * <p>
 *
 * <pre>
 * Note
 * - 일반적인 성공/실패에 따른 분기처리
 *   (기능 설명을 위해 간단하게 tasklet으로 구현했다)
 * </pre>
 */
@Configuration
@Slf4j
public class SampleTaskletJob2Config extends BaseAbstractJob {

	/**
	 * Job Name
	 */
	private static final String JOB_NAME = "sample-tasklet-job2";

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
	public Job BatchJob() {
		return getJobBuilder()
				.start(BatchJobStep1())
				.on("FAILED").to(BatchJobStep3()) // FAILED 일 경우  step3으로 이동.
				.on("*").end() // step3의 결과에 관계 없이 Flow 종료.
				.from(BatchJobStep1())
				.on("*").to(BatchJobStep2()) // FAILED 외에 모든 경우 step2로 이동.
				.on("*").end() // step2의 결과에 관계 없이 Flow 종료.
				.end()
				.build();
	}

	/**
	 * Description : Job에서 사용할 Step1 Bean을 생성한다.
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_step1")
	public Step BatchJobStep1() {

		return stepBuilder.get(JOB_NAME + "_step1").tasklet((stepContribution, chunkContext) -> {

			// Random 숫자를 사용하여 ExitStatus 값을 임의로 수정
			int rNum = Integer.parseInt(RandomStringUtils.randomNumeric(2));
			if (rNum % 2 == 0) {
				stepContribution.setExitStatus(ExitStatus.FAILED);
			} else {
				stepContribution.setExitStatus(ExitStatus.COMPLETED);
			}

			if (log.isDebugEnabled()) {
				log.debug("> BatchJobStep1 : {}, ExitStatus : {}", rNum, stepContribution.getExitStatus().getExitCode());
			}

			//작업완료 상태코드를 Return한다.
			return RepeatStatus.FINISHED;
		}).build();
	}

	/**
	 * Description : Job에서 사용할 Step2 Bean을 생성한다.
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_step2")
	public Step BatchJobStep2() {

		return stepBuilder.get(JOB_NAME + "_step2").tasklet((stepContribution, chunkContext) -> {

			log.debug(">> BatchJobStep2");

			//작업완료 상태코드를 Return한다.
			return RepeatStatus.FINISHED;
		}).build();
	}

	/**
	 * Description : Job에서 사용할 Step3 Bean을 생성한다.
	 * <p>
	 * @return
	 */
	@Bean(name = JOB_NAME + "_step3")
	public Step BatchJobStep3() {

		return stepBuilder.get(JOB_NAME + "_step3").tasklet((stepContribution, chunkContext) -> {

			log.debug(">> BatchJobStep3");

			//작업완료 상태코드를 Return한다.
			return RepeatStatus.FINISHED;
		}).build();
	}
}