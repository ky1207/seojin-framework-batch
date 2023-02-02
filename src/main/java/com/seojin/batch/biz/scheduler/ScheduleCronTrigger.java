package com.seojin.batch.biz.scheduler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;

import com.seojin.batch.sys.base.BaseBean;
import org.quartz.JobDataMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seojin.batch.sys.base.BaseBean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * ScheduleCronTrigger
 */
@Slf4j
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ScheduleCronTrigger extends BaseBean {

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
	 * parameters
	 */
	private String parameters;

	/**
	 * description
	 */
	private String description;

	/**
	 * isNotification
	 */
	private String isNotification;

	/**
	 * notificationRecipient
	 */
	private String notificationRecipient;

	/**
	 * startDtm
	 */
	private Date startDtm;

	/**
	 * endDtm
	 */
	private Date endDtm;

	/**
	 * useYn
	 */
	private String useYn;

	/**
	 * rgsterNo
	 */
	private String rgsterNo;
	/**
	 * moderNo
	 */
	private String moderNo;

	/**
	 * rgstDtm
	 */
	private Date rgstDtm;
	/**
	 * modDtm
	 */
	private Date modDtm;

	/**
	 * previous FireTime
	 */
	private Long prevFireTime;

	/**
	 * next FireTime
	 */
	private Long nextFireTime;

	/**
	 * trigger State
	 */
	private String triggerState;

	/**
	 * jobData
	 */
	@JsonIgnore
	private byte[] jobData;

	/**
	 * cron Expression
	 */
	private String cronExpression;

	/**
	 * job State
	 */
	private int jobState;

	/**
	 * Job Data Map
	 */
	public JobDataMap getJobDataMap() {
		JobDataMap jobDataMap = null;

		ByteArrayInputStream bais = new ByteArrayInputStream(jobData);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			jobDataMap = (JobDataMap) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			log.error("load failed JobData", e);
		}

		return jobDataMap;
	}


}
