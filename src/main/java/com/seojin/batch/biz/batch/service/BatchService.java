package com.seojin.batch.biz.batch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.seojin.batch.biz.batch.mapper.BatchMapper;

/**
 * Description : 실행결과 Update를 위한 Service (수정금지)
 * <p>
 *
 * <pre>
 * Note
 * -  
 * </pre>
 */
@Service
public class BatchService {

	/**
	 * CommonLogMapper
	 */
	@Autowired
	private BatchMapper batchMapper;

	/**
	 * QRTZ_TRIGGERS_EXECUTION_EXT table에 triggersExecutionId를 update하는
	 * commonLogMapper.updateJobExecutionId()를 호출한다.
	 * 
	 * @param triggersExecutionId
	 * @param jobExecutionId
	 * @throws CommonLogException
	 */
	public void updateJobExecutionId(Long triggersExecutionId, Long jobExecutionId) throws Exception {
		batchMapper.updateJobExecutionId(triggersExecutionId, jobExecutionId);
	}

}
