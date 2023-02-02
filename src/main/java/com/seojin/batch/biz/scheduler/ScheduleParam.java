package com.seojin.batch.biz.scheduler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import com.seojin.commons.base.Paging;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Schedule Param
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class ScheduleParam extends Paging {

	private static final long serialVersionUID = -4364147940595727430L;

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
	 * lastUpdated
	 */
	private Date rgstDtm;

	/**
	 * rgsterNo
	 */
	private String moderNo;

	/**
	 * lastUpdated
	 */
	private Date modDtm;

	/**
	 * cron Expression
	 */
	private String cronExpression;

	/**
	 * description
	 */
	private String description;

	/**
	 * executor
	 */
	private String executor;

	/**
	 * jobExecutionId
	 */
	private Long jobExecutionId;

	/**
	 * triggerState
	 */
	private String triggerState;

	/**
	 * insert Yn
	 */
	private String insertYn;

	public void setDescription(String description) {
		if (null != description) {
			try {
				this.description = URLDecoder.decode(description, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				this.description = description;
			}
		}
	}

}
