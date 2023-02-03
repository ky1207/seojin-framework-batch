package com.seojin.batch.biz.batch;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.seojin.batch.sys.base.BaseJobController;

/**
 * Description : Batch Common Controller (수정금지)
 * <p>
 *
 * <pre>
 * Note
 * -  Batch Job 실행을 위한 공통 Controller
 * -  Scheduler 미 사용시 : getBatchJobLaunch 사용 ( http(s)://batch-domain:post/batch/{jobName}?param1=123&param2=abc 로 호출 )
 * </pre>
 */
@RestController
public class BatchController extends BaseJobController {

	/**
	 * ApplicationContext
	 */
	@Autowired
	private ApplicationContext context;

	/**
	 * Description : Batch Job Main Launch
	 * <p>
	 * @param jobName
	 * @param jobParam
	 * @return
	 */
	@GetMapping(value = "/batch/{jobName}")
	public ResponseEntity<Map<String, Object>> getBatchJobLaunch(HttpServletRequest request, @PathVariable(value = "jobName") String jobName) {

		Job job = context.getBean(jobName, Job.class);

		JobParametersBuilder jobParamsBuilder = new JobParametersBuilder();
		setParameters(jobParamsBuilder, request.getQueryString());

		JobParameters jobParams = jobParamsBuilder.toJobParameters();
		return getJobExecution(job, jobParams);
	}

	/**
	 * Description : Scheduler에서 호출되는 Batch Job Main Launch
	 * <p>
	 * @param jobName
	 * @param executionid
	 * @param batchParam
	 * @return
	 */
	@PostMapping(value = "/batch/{jobName}/{executionid}")
	public ResponseEntity<Map<String, Object>> postBatchJobLaunch(@PathVariable(value = "jobName") String jobName,
			@PathVariable(value = "executionid") String executionid,
			@RequestBody MultiValueMap<String, String> batchParam) {

		Job job = context.getBean(jobName, Job.class);

		JobParametersBuilder jobParamsBuilder = new JobParametersBuilder();
		jobParamsBuilder.addString("executionId", executionid);
		setParameters(jobParamsBuilder, String.valueOf(batchParam.getFirst("parameters")));

		JobParameters jobParams = jobParamsBuilder.toJobParameters();
		return getJobExecution(job, jobParams);
	}

	/**
	 * Description : Root URL 대응
	 * <p>
	 * @return
	 */
	@GetMapping(value = "")
	public ResponseEntity<Integer> batchLaunch() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Description : job Params 설정
	 * <p>
	 * @param jobParamsBuilder
	 * @param params
	 */
	private void setParameters(JobParametersBuilder jobParamsBuilder, String params) {
		int i = 0;

		jobParamsBuilder.addDate("runDate", new Date());

		if (params != null && params.trim().length() > 0) {
			if (params.indexOf('&') > 0) {
				for (String param : params.split("&")) {
					if (param.indexOf('=') > 0) {
						String key = param.split("=")[0];
						String value = param.split("=")[1];

						jobParamsBuilder.addString(key, value);
					} else {
						i++;
						jobParamsBuilder.addString("jobParam" + i, param);
					}
				}
			} else {
				i++;
				jobParamsBuilder.addString("jobParam" + i, params);
			}
		}
	}
}