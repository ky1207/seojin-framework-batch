package com.seojin.batch.biz.scheduler.mapper;

import java.util.List;

import com.seojin.batch.biz.scheduler.*;
import com.seojin.batch.sys.database.annotation.PrimaryBatchMapper;
import com.seojin.commons.annotations.mybatis.PrimaryMapper;
import org.quartz.SchedulerException;


/**
 * SchedulerMapper
 */
@PrimaryBatchMapper
public interface SchedulerMapper {

    /**
     * Schedules List 검색 Count
     */
    int selectScheduleListCount(ScheduleParam scheduleParam);

    /**
     * Schedules List 검색
     */
    List<ScheduleCronTrigger> selectScheduleList(ScheduleParam scheduleParam);

    /**
     * Schedule detail
     */
    ScheduleCronTrigger selectSchedule(ScheduleParam scheduleParam);

    /**
     * Schedule insert
     */
    void insertJobDetailsExtend(ScheduleParam scheduleParam) throws SchedulerException;

    /**
     * Schedule update
     */
    void updateJobDetailsExtend(ScheduleParam scheduleParam) throws SchedulerException;

    /**
     * Schedule unused
     */
    void deleteJobDetailsExtend(ScheduleParam scheduleParam) throws SchedulerException;

    /**
     * schedule log list Count
     */
    Integer selectScheduleLogCount(ScheduleParam scheduleParam) throws SchedulerException;

    /**
     * schedule log list
     */
    List<TriggerExecution> selectScheduleLog(ScheduleParam scheduleParam) throws SchedulerException;

    /**
     * batch job log list Count
     */
    Integer selectBatchJobLogCount(ScheduleParam scheduleParam) throws SchedulerException;

    /**
     * batch job log list
     */
    List<JobExecution> selectBatchJobLog(ScheduleParam scheduleParam) throws SchedulerException;

    /**
     * batch step log count
     */
    Integer selectBatchStepLogCount(ScheduleParam scheduleParam) throws SchedulerException;

    /**
     * batch step log
     */
    List<StepExecution> selectBatchStepLog(ScheduleParam scheduleParam) throws SchedulerException;

    /**
     * Quartz trigger execution log insert
     */
    void insertTriggerExecution(TriggerExecution triggerExecution) throws SchedulerException;

    /**
     * Quartz trigger execution log update
     */
    void updateTriggerExecution(TriggerExecution triggerExecution) throws SchedulerException;

    /**
     * Quartz trigger execution log delete
     */
    void deleteTriggerExecution(ScheduleParam scheduleParam) throws SchedulerException;

}
