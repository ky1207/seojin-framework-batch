package com.seojin.batch.biz.batch.mapper;

import org.apache.ibatis.annotations.Param;

import com.seojin.batch.sys.database.annotation.PrimaryBatchMapper;

/**
 * Description : 실행결과 Update를 위한 BatchMapper (수정금지)
 * <p>
 *
 * <pre>
 * Note
 * -  Batch Job은 @PrimaryBatchMapper를 사용한다.
 * </pre>
 */
@PrimaryBatchMapper
public interface BatchMapper {

	/**
	 * QRTZ_TRIGGERS_EXECUTION_EXT table에 triggersExecutionId를 update한다.
	 * 
	 * @param triggersExecutionId
	 * @param jobExecutionId
	 */
	void updateJobExecutionId(@Param("triggersExecutionId") Long triggersExecutionId, @Param("jobExecutionId") Long jobExecutionId);

}
