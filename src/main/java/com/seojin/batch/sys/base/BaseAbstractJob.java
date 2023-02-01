package com.seojin.batch.sys.base;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;

import com.seojin.batch.biz.batch.service.BatchService;
import com.seojin.batch.sys.listener.BatchJobLoggingListener;

/**
 * Description : Base Job config를 생성한다.  
 * <p>
 *
 */
public abstract class BaseAbstractJob {

	/**
	 * default chunk size
	 */
	protected static final int DEFAULT_CHUNK_SIZE = 1000;

	/**
	 * task pool size
	 */
	protected static final int CORE_TASK_POOL_SIZE = 2;

	/**
	 * max task pool size
	 */
	protected static final int MAX_TASK_POOL_SIZE = 4;

	/**
	 * JobBuilderFactory
	 */
	@Autowired
	public JobBuilderFactory jobBuilder;

	/**
	 * StepBuilderFactory
	 */
	@Autowired
	public StepBuilderFactory stepBuilder;

	/**
	 * BatchService
	 */
	@Autowired
	private BatchService batchService;

	/**
	 * jobName abstract method
	 * 
	 * @return
	 */
	protected abstract String jobName();

	/**
	 * Description : preventRestart, CommonLogJobListener, RunIdIncrementer를 갖는 JobBuilder를  return한다.
	 * <p>
	 * @return
	 */
	protected JobBuilder getJobBuilder() {
		return jobBuilder.get(jobName()).preventRestart().listener(new BatchJobLoggingListener(batchService))
				.incrementer(new RunIdIncrementer());
	}

}
