package com.seojin.batch.biz.sample.tasklet;

import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Description : Sample Tasklet DTO
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SampleTaskletJob1 {
	/**
	 * 상세번호
	 */
	private String dtlNo;

	/**
	 * 상세명
	 */
	private String dtlDesc;

	/**
	 * 정렬순번
	 */
	private int sortSeq;

	/**
	 * 사용여부
	 */
	private String useYn;

	/**
	 * 등록자 
	 */
	private String rgsterNo;

	/**
	 * 등록일
	 */
	private Timestamp rgstDtm;

}
