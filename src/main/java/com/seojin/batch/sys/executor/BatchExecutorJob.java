package com.seojin.batch.sys.executor;

import com.seojin.batch.biz.scheduler.TriggerExecution;
import com.seojin.batch.biz.scheduler.service.SchedulerService;
import com.seojin.batch.sys.constant.Const;
import com.seojin.commons.utils.UtilsString;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.jdbcjobstore.Constants;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

/**
 * SpringBatchShellExecutorJob
 */
@DisallowConcurrentExecution
@Slf4j
public class BatchExecutorJob extends QuartzJobBean {
    /**
     * ApplicationContext
     */
    private ApplicationContext appContext;

    /**
     * jobLauncher
     */
    public JobLauncher jobLauncher;

    /**
     * SchedulerService
     */
    private SchedulerService schedulerService;

    @Autowired
    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Autowired
    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    @Autowired
    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            String jobName = context.getJobDetail().getKey().getName();

            TriggerExecution triggerExecution = getTriggerExecution(context, jobName);
            schedulerService.insertTriggerExecution(triggerExecution);

            String executionId = triggerExecution.getTriggersExecutionId().toString();
            String parameters = context.getTrigger().getJobDataMap().getString(Const.SCHEDULE_PARAMS);

            Job job =  appContext.getBean(jobName, Job.class);

            JobParametersBuilder jobParamsBuilder = new JobParametersBuilder();
            jobParamsBuilder.addString("executionId", executionId);
            setParameters(jobParamsBuilder, parameters);

            JobParameters jobParams = jobParamsBuilder.toJobParameters();

            try {
                JobExecution jobExecution = jobLauncher.run( job, jobParams);

                if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                    triggerExecution.setExitCode("200");
                    triggerExecution.setExitMessage("Success");
                } else {
                    triggerExecution.setExitCode("500");
                    triggerExecution.setExitMessage("Fail");
                }

            } catch (Exception e) { // NOPMD
                triggerExecution.setExitCode("500");
                triggerExecution.setExitMessage(e.getMessage());
            } finally {
                context.setResult(triggerExecution);
            }

            if (log.isDebugEnabled()) {
                log.debug("==============================================================");
                log.debug(" > SpringBatchShellExecutorJob jobName :: {}", jobName);
                log.debug(" > triggerExecution :: {}", triggerExecution);
                log.debug("==============================================================");
            }

        } catch (SchedulerException se) {
            throw new JobExecutionException(se);
        }
    }

    /**
     * Description : set triggerExecution
     * <p>
     * @param context
     * @param command
     * @return
     */
    public TriggerExecution getTriggerExecution(JobExecutionContext context, String jobName) {
        Trigger trigger = context.getTrigger();
        String schedulerName = getSchedulerName(context);
        String executor = trigger.getJobDataMap().getString(Const.SCHEDULE_EXECUTOR);

        TriggerExecution triggerExecution = (TriggerExecution) context.getResult();
        if (triggerExecution == null) {
            triggerExecution = new TriggerExecution();
        }

        triggerExecution.setSchedName(schedulerName);
        triggerExecution.setJobName(context.getJobDetail().getKey().getName());
        triggerExecution.setJobGroup(context.getJobDetail().getKey().getGroup());
        triggerExecution.setTriggerName(trigger.getKey().getName());
        triggerExecution.setTriggerGroup(trigger.getKey().getGroup());
        triggerExecution.setTriggerType(trigger instanceof CronTriggerImpl ? Constants.TTYPE_CRON : Constants.TTYPE_SIMPLE);
        triggerExecution.setCommand(jobName);
        triggerExecution.setParameters(context.getTrigger().getJobDataMap().getString(Const.SCHEDULE_PARAMS));
        triggerExecution.setExecutor(UtilsString.isEmpty(executor) ? schedulerName : executor);
        triggerExecution.setStartTime(context.getFireTime());
        triggerExecution.setEndTime(new Date());

        return triggerExecution;
    }

    private String getSchedulerName(JobExecutionContext context) {
        String schedulerName = "";
        try {
            schedulerName = context.getScheduler().getSchedulerName();
        } catch (SchedulerException e) {
            log.error("Get a handle to the Scheduler instance failed.", e);
        }

        return schedulerName;
    }

    /**
     * Description : job Params 설정
     * <p>
     * @param jobParamsBuilder
     * @param params
     */
    private void setParameters(JobParametersBuilder jobParamsBuilder, String params) {
        int i = 0;

        jobParamsBuilder.addDate("runDate", new Date());

        if (params != null && params.trim().length() > 0) {
            if (params.indexOf('&') > 0) {
                for (String param : params.split("&")) {
                    if (param.indexOf('=') > 0) {
                        String key = param.split("=")[0];
                        String value = param.split("=")[1];

                        jobParamsBuilder.addString(key, value);
                    } else {
                        i++;
                        jobParamsBuilder.addString("jobParam" + i, param);
                    }
                }
            } else {
                i++;
                jobParamsBuilder.addString("jobParam" + i, params);
            }
        }
    }

}
