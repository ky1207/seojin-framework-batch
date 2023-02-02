package com.seojin.batch.biz.scheduler;

import java.util.Date;

import lombok.Data;

/**
 * TriggerExecution
 */
@Data
public class TriggerExecution {

	/**
	 * 일련번호
	 */
	private Long triggersExecutionId;

	/**
	 * spring batch job execution id
	 */
	private Long jobExecutionId;

	/**
	 * quartz scheduler name
	 */
	private String schedName;

	/**
	 * quartz job name
	 */
	private String jobName;

	/**
	 * quartz job group
	 */
	private String jobGroup;

	/**
	 * quartz trigger name
	 */
	private String triggerName;

	/**
	 * quartz trigger group
	 */
	private String triggerGroup;

	/**
	 * quartz trigger type
	 */
	private String triggerType;

	/**
	 * 스프링배치 실행명령
	 */
	private String command;

	/**
	 * 스프링배치 PARAMETERS
	 */
	private String parameters;

	/**
	 * 실행자
	 */
	private String executor;

	/**
	 * Exit Code
	 */
	private String exitCode;

	/**
	 * Exit Message
	 */
	private String exitMessage;

	/**
	 * 시작일시
	 */
	private Date startTime;

	/**
	 * 종료일시
	 */
	private Date endTime;

}
