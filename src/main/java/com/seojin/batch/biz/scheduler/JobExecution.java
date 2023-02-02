package com.seojin.batch.biz.scheduler;

import java.util.Date;
import java.util.List;


import com.seojin.batch.sys.base.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Job Execution
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JobExecution extends BaseBean {

	/**
	 * job Execution Id
	 */
	private Long jobExecutionId;

	/**
	 * quartz job name
	 */
	private String jobName;

	/**
	 * version
	 */
	private Long version;

	/**
	 * job Instance Id
	 */
	private Long jobInstanceId;

	/**
	 * create Time
	 */
	private Date createTime;

	/**
	 * startTime
	 */
	private Date startTime;

	/**
	 * endTime
	 */
	private Date endTime;

	/**
	 * status
	 */
	private String status;

	/**
	 * exit Code
	 */
	private String exitCode;

	/**
	 * exit Message
	 */
	private String exitMessage;

	/**
	 * last Updated
	 */
	private Date lastUpdated;

	/**
	 * job Configuration Location
	 */
	private String jobConfigurationLocation;

	/**
	 * step Executions
	 */
	private List<StepExecution> stepExecutions;
}
