package com.seojin.batch.sys.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import com.seojin.batch.biz.batch.service.BatchService;

import lombok.extern.slf4j.Slf4j;

/**
 * Description : job logging
 * <p>
 *
 */
@Slf4j
public class BatchJobLoggingListener implements JobExecutionListener {
	/**
	 * CommonLogService
	 */
	private final BatchService batchService;

	/**
	 * @param service
	 */
	public BatchJobLoggingListener(BatchService service) {
		batchService = service;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		// beforeJob
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		try {
			String triggersExecutionId = jobExecution.getJobParameters().getString("executionId");
			Long jobExecutionId = jobExecution.getId();

			if (triggersExecutionId != null) {
				batchService.updateJobExecutionId(Long.valueOf(triggersExecutionId), jobExecutionId);
			}
		} catch (Exception e) {
			log.error("update jobExecutionId failed", e);
		}
	}
}
