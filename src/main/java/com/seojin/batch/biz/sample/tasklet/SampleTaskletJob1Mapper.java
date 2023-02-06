package com.seojin.batch.biz.sample.tasklet;

import java.util.List;

import com.seojin.batch.sys.database.annotation.PrimaryBatchMapper;

/**
 * Description : SampleTaskletJob PrimaryMapper
 * <p>
 *
 * <pre>
 * Note
 * -  Batch Job은 @PrimaryBatchMapper를 사용한다.
 * </pre>
 */
@PrimaryBatchMapper
public interface SampleTaskletJob1Mapper {
	/**
	 * Description : select BatchSample
	 * <p>
	 * @return
	 */
	List<SampleTaskletJob1> selectBatchSample();

	/**
	 * Description : update BatchSample
	 * <p>
	 * @param sampleTaskletJob1
	 * @return
	 */
	int updateBatchSample(SampleTaskletJob1 sampleTaskletJob1);
}
