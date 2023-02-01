package com.seojin.batch.sys.base;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.seojin.batch.sys.exception.DefaultExceptionAttributes;
import com.seojin.batch.sys.exception.ExceptionAttributes;

import lombok.extern.slf4j.Slf4j;

/**
 * Description : Base JobController
 * <p>
 *
 */
@Slf4j
public class BaseJobController {

	/**
	 * jobLauncher
	 */
	private JobLauncher jobLauncher;
	@Autowired
	public void setJobLauncher(JobLauncher jobLauncher) {
		this.jobLauncher = jobLauncher;
	}

	/**
	 * Description : JobParameters가 필요없는 Job 실행
	 * <p>
	 * @param job
	 * @return
	 */
	public ResponseEntity<Map<String, Object>> getJobExecution(Job job) {
		JobParameters jobParams = new JobParametersBuilder()
				.addDate("runDate", new Date())
				.toJobParameters();

		return getJobExecution(job, jobParams);
	}

	/**
	 * Description : JobParameters가 필요한 Job 실행
	 * <p>
	 * @param job
	 * @param jobParameters
	 * @return
	 */
	public ResponseEntity<Map<String, Object>> getJobExecution(Job job, JobParameters jobParameters) {
		HttpStatus resStatus = HttpStatus.OK;
		Map<String, Object> resBody = new HashMap<String, Object>();
		ExceptionAttributes exceptionAttributes = new DefaultExceptionAttributes();

		try {
			JobExecution jobExecution = jobLauncher.run(job, jobParameters);

			if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
				resBody = exceptionAttributes.getExceptionAttributes("JOB 실행 완료", resStatus);
			} else {
				resStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				resBody = exceptionAttributes.getExceptionAttributes("JOB 실행 오류", resStatus);
			}
		} catch (JobExecutionException e) {
			if (log.isErrorEnabled()) {
				log.error("==> Job name : {}, Exception : {}", job.getName(), e);
			}

			resStatus = HttpStatus.INTERNAL_SERVER_ERROR;

			//보안 취약점(오류메시지를 통한 정보노출) 대응			
			resBody = exceptionAttributes.getExceptionAttributes("JOB 실행 오류", HttpStatus.INTERNAL_SERVER_ERROR);

		}

		return new ResponseEntity<Map<String, Object>>(resBody, resStatus);
	}

	/**
	 * Description :  etc Exception Handler
	 * <p>
	 * @param exception
	 * @param request
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleException(Exception exception, HttpServletRequest request) {

		ExceptionAttributes exceptionAttributes = new DefaultExceptionAttributes();
		Map<String, Object> resBody = exceptionAttributes.getExceptionAttributes("Internal Server Error!!", HttpStatus.INTERNAL_SERVER_ERROR);

		if (log.isDebugEnabled()) {
			log.debug("==> handleException : {}", exception);
		}

		return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
