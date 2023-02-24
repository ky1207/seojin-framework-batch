package com.seojin.batch.biz.scheduler.service;

import com.seojin.batch.biz.scheduler.*;
import com.seojin.batch.biz.scheduler.mapper.SchedulerMapper;
import com.seojin.batch.sys.constant.Const;
import com.seojin.batch.sys.executor.BatchExecutorJob;
import com.seojin.batch.sys.listener.CommonLogTriggerListener;
import com.seojin.commons.grid.Grid;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.KeyMatcher;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * SchedulerService
 */
@Slf4j
@Service
@Transactional
public class SchedulerService {

    /**
     * scheduler
     */
    private Scheduler scheduler;

    /**
     * schedulerMapper
     */
    private SchedulerMapper schedulerMapper;

    /**
     * schedulerFactoryBean
     */
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    public void setSchedulerMapper(SchedulerMapper schedulerMapper) {
        this.schedulerMapper = schedulerMapper;
    }

    @Autowired
    public void setSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    /**
     * initScheduler
     */
    @PostConstruct
    public void initScheduler() {
        scheduler = schedulerFactoryBean.getScheduler();
    }

    /**
     * Description : Schedules List 검색
     * <p>
     *
     * @param param
     * @return
     * @throws SchedulerException
     */
    public Grid findScheduleList(ScheduleParam scheduleParam) throws SchedulerException {
        int count = schedulerMapper.selectScheduleListCount(scheduleParam);

        List<ScheduleCronTrigger> list = Collections.emptyList();
        if (count > 0) {
            list = schedulerMapper.selectScheduleList(scheduleParam);
        }

        Grid grid = new Grid();
        grid.setTotal(count);
        grid.setData(list);

        return grid;
    }

    /**
     * Description : Schedule detail
     * <p>
     *
     * @param param
     * @return
     * @throws SchedulerException
     */
    public ScheduleCronTrigger findSchedule(ScheduleParam scheduleParam) throws SchedulerException {
        scheduleParam.setSchedName(scheduler.getSchedulerName());

        return schedulerMapper.selectSchedule(scheduleParam);
    }

    /**
     * Description : Schedule insert
     * <p>
     *
     * @param scheduleParam
     * @throws SchedulerException
     */
    public void inputSchedule(ScheduleParam scheduleParam) throws SchedulerException {
        JobDetail jobDetail = createJobDetail(scheduleParam);
        CronTrigger trigger = createCronTrigger(scheduleParam);

        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.pauseTrigger(trigger.getKey());

        //custom data table update
        scheduleParam.setSchedName(scheduler.getSchedulerName());
        scheduleParam.setJobName(jobDetail.getKey().getName());
        scheduleParam.setJobGroup(jobDetail.getKey().getGroup());

        schedulerMapper.insertJobDetailsExtend(scheduleParam);
    }

    /**
     * Description : Schedule update
     * <p>
     *
     * @param param
     * @throws SchedulerException
     */
    public void modifySchedule(ScheduleParam scheduleParam) throws SchedulerException {
        JobDetail jobDetail = createJobDetail(scheduleParam);
        scheduler.addJob(jobDetail, true, true);

        TriggerKey triggerKey = getTriggerKey(scheduleParam);
        CronTrigger newTrigger = createCronTrigger(scheduleParam);
        addTriggerListener(triggerKey);
        scheduler.rescheduleJob(triggerKey, newTrigger);
        scheduler.pauseTrigger(newTrigger.getKey());

        //custom data table update
        scheduleParam.setSchedName(scheduler.getSchedulerName());
        scheduleParam.setJobName(jobDetail.getKey().getName());
        scheduleParam.setJobGroup(jobDetail.getKey().getGroup());

        schedulerMapper.updateJobDetailsExtend(scheduleParam);
    }

    /**
     * Description : Schedule resume
     * <p>
     *
     * @param scheduleParam
     * @throws SchedulerException
     */
    public void setScheduleResume(ScheduleParam scheduleParam) throws SchedulerException {
        TriggerKey triggerKey = getTriggerKey(scheduleParam);
        addTriggerListener(triggerKey);
        scheduler.resumeTrigger(triggerKey);
    }

    /**
     * Description : Schedule Pause
     * <p>
     *
     * @param scheduleParam
     * @throws SchedulerException
     */
    public void setSchedulePause(ScheduleParam scheduleParam) throws SchedulerException {
        TriggerKey triggerKey = getTriggerKey(scheduleParam);
        scheduler.pauseTrigger(triggerKey);
    }

    /**
     * Description : Schedule execute
     * <p>
     *
     * @param scheduleParam
     * @throws SchedulerException
     */
    public void inputSimpleSchedule(ScheduleParam scheduleParam) throws SchedulerException {
        JobKey jobKey = getJobKey(scheduleParam);
        SimpleTrigger simpleTrigger = createSimpleTriggerForJob(scheduleParam);
        addTriggerListener(simpleTrigger.getKey());
        scheduler.scheduleJob(simpleTrigger);

        scheduler.getTriggersOfJob(jobKey).forEach(trigger -> {
            if (trigger instanceof SimpleTriggerImpl) {
                try {
                    scheduler.resumeTrigger(trigger.getKey());
                } catch (SchedulerException e) {
                    log.error("execute trigger failed::{}", e);
                }
            }
        });
    }

    /**
     * Description : Schedule unused
     * <p>
     *
     * @param scheduleParam
     * @throws SchedulerException
     */
    public void deleteSchedule(ScheduleParam scheduleParam) throws SchedulerException {
        scheduleParam.setSchedName(scheduler.getSchedulerName());

        schedulerMapper.deleteJobDetailsExtend(scheduleParam);
        schedulerMapper.deleteTriggerExecution(scheduleParam);

        TriggerKey triggerKey = getTriggerKey(scheduleParam);
        scheduler.unscheduleJob(triggerKey);
    }

    /**
     * Description : schedule log list
     * <p>
     *
     * @param param
     * @return
     * @throws SchedulerException
     */
    public Grid findScheduleLogList(ScheduleParam param) throws SchedulerException {
        int count = schedulerMapper.selectScheduleLogCount(param);

        List<TriggerExecution> list = Collections.emptyList();
        if (count > 0) {
            list = schedulerMapper.selectScheduleLog(param);
        }

        Grid grid = new Grid();
        grid.setTotal(count);
        grid.setData(list);

        return grid;
    }

    /**
     * Description : batch job log list
     * <p>
     *
     * @param batchParam
     * @return
     * @throws BatchJobException
     */
    public Grid findBatchJobLog(ScheduleParam scheduleParam) throws SchedulerException {
        int count = schedulerMapper.selectBatchJobLogCount(scheduleParam);

        List<JobExecution> list = Collections.emptyList();
        if (count > 0) {
            list = schedulerMapper.selectBatchJobLog(scheduleParam);
        }

        Grid grid = new Grid();
        grid.setTotal(count);
        grid.setData(list);

        return grid;
    }

    /**
     * Description : batch step log list
     * <p>
     *
     * @param param
     * @return
     * @throws BatchJobException
     */
    public Grid findBatchStepLogList(ScheduleParam scheduleParam) throws SchedulerException {
        int count = schedulerMapper.selectBatchStepLogCount(scheduleParam);

        List<StepExecution> list = Collections.emptyList();
        if (count > 0) {
            list = schedulerMapper.selectBatchStepLog(scheduleParam);
        }

        Grid grid = new Grid();
        grid.setTotal(count);
        grid.setData(list);

        return grid;
    }

    /**
     * Description : insertTriggerExecution
     * <p>
     *
     * @param triggerExecution
     * @throws SchedulerException
     */
    public void insertTriggerExecution(TriggerExecution triggerExecution) throws SchedulerException {
        schedulerMapper.insertTriggerExecution(triggerExecution);
    }

    /**
     * Description : updateTriggerExecution
     * <p>
     *
     * @param triggerExecution
     * @throws SchedulerException
     */
    public void updateTriggerExecution(TriggerExecution triggerExecution) throws SchedulerException {
        schedulerMapper.updateTriggerExecution(triggerExecution);
    }

    private JobDetail createJobDetail(ScheduleParam scheduleParam) {
        return JobBuilder.newJob(BatchExecutorJob.class)
                .withIdentity(scheduleParam.getJobName(), scheduleParam.getJobGroup())
                .withDescription(scheduleParam.getDescription())
                .build();
    }

    private CronTrigger createCronTrigger(ScheduleParam scheduleParam) {
        TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(scheduleParam.getJobName(), scheduleParam.getJobGroup())
                .usingJobData(getJobDataMap(scheduleParam))
                .withDescription(scheduleParam.getDescription())
                .withSchedule(cronSchedule(scheduleParam.getCronExpression()));

        if (scheduleParam.getStartDtm() != null) {
            triggerBuilder.startAt(scheduleParam.getStartDtm());
        }

		/*
		- endAt 설정시 해당시간이후에 Trigger 정보를 DB 에서 자동삭제하므로 사용안함.
		*/
//		if (scheduleParam.getEndDtm() != null) {
//			triggerBuilder.endAt(scheduleParam.getEndDtm());
//		}

        return triggerBuilder.build();
    }

    private TriggerKey getTriggerKey(ScheduleParam scheduleParam) {
        return new TriggerKey(scheduleParam.getJobName(), scheduleParam.getJobGroup());
    }

    /**
     * Description : add TriggerListener
     * <p>
     *
     * @param triggerKey
     * @throws SchedulerException
     */
    public void addTriggerListener(TriggerKey triggerKey) throws SchedulerException {
        scheduler.getListenerManager().addTriggerListener(new CommonLogTriggerListener(triggerKey.toString(), this),
                KeyMatcher.keyEquals(triggerKey));
    }

    private ScheduleBuilder<CronTrigger> cronSchedule(String cronExpression) {
        return CronScheduleBuilder.cronSchedule(cronExpression);
    }

    private JobDataMap getJobDataMap(ScheduleParam scheduleParam) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(Const.SCHEDULE_PARAMS, scheduleParam.getParameters());
        jobDataMap.put(Const.SCHEDULE_EXECUTOR, scheduleParam.getExecutor());

        return jobDataMap;
    }

    private JobKey getJobKey(ScheduleParam scheduleParam) {
        return new JobKey(scheduleParam.getJobName(), scheduleParam.getJobGroup());
    }

    private SimpleTrigger createSimpleTriggerForJob(ScheduleParam scheduleParam) {
        JobKey jobKey = getJobKey(scheduleParam);

        return (SimpleTrigger) TriggerBuilder.newTrigger().usingJobData(getJobDataMap(scheduleParam))
                .forJob(jobKey).startNow().build();

    }

}
