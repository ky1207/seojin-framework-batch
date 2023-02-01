package com.seojin.batch.biz.scheduler;

import java.util.Date;

import com.seojin.batch.sys.base.BaseBean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author 3top
 *
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class StepExecution extends BaseBean {

	private static final long serialVersionUID = -743021570372797893L;

	/**
	 * 일련번호
	 */
	private Long stepExecutionId;

	/**
	 * version
	 */
	private Long version;

	/**
	 * step Name
	 */
	private String stepName;

	/**
	 * spring batch job execution id
	 */
	private Long jobExecutionId;

	/**
	 * start Time
	 */
	private Date startTime;

	/**
	 * end Time
	 */
	private Date endTime;

	/**
	 * 실행상태 : COMPLETED, STARTED, ETC
	 */
	private String status;

	/**
	 * commit Count
	 */
	private Long commitCount;

	/**
	 * read Count
	 */
	private Long readCount;

	/**
	 * filter Count
	 */
	private Long filterCount;

	/**
	 * write Count
	 */
	private Long writeCount;

	/**
	 * read Skip Count
	 */
	private Long readSkipCount;

	/**
	 * write Skip Count
	 */
	private Long writeSkipCount;

	/**
	 * process Skip Count
	 */
	private Long processSkipCount;

	/**
	 * rollback Count
	 */
	private Long rollbackCount;

	/**
	 * 종료코드
	 */
	private String exitCode;

	/**
	 * 에러메시지
	 */
	private String exitMessage;

	/**
	 * last Updated
	 */
	private Date lastUpdated;
}
