package com.seojin.batch.sys.runner;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.seojin.batch.biz.scheduler.TriggerExecution;
import com.seojin.batch.sys.constant.Const;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.calendar.CronCalendar;
import org.quartz.impl.jdbcjobstore.Constants;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.seojin.batch.biz.scheduler.service.SchedulerService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * SchedulerApplicationRunner
 */
@Slf4j
@Component
public class SchedulerApplicationRunner implements ApplicationRunner {

	/**
	 * SchedulerFactoryBean
	 */
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	/**
	 * SchedulerService
	 */
	@Autowired
	private SchedulerService schedulerService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		List<String> triggerGroups = scheduler.getTriggerGroupNames();

		triggerGroups.forEach(triggerGroup -> {
			try {
				Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.groupEquals(triggerGroup));
				triggerKeys.forEach(triggerKey -> {
					try {
						schedulerService.addTriggerListener(triggerKey);
						Trigger trigger = scheduler.getTrigger(triggerKey);

						if (log.isDebugEnabled()) {
							TriggerState triggerState = scheduler.getTriggerState(triggerKey);

							log.debug("==============================================================");
							log.debug(" > SchedulerApplicationRunner run ");
							log.debug(" > trigger info::{},{}", trigger, triggerState);
							log.debug("==============================================================");
						}

						if (trigger instanceof CronTriggerImpl) {
							CronTriggerImpl cronTrigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
							cronTrigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
							Date rescedule = scheduler.rescheduleJob(triggerKey, cronTrigger);

							if (log.isDebugEnabled()) {
								log.debug("==============================================================");
								log.debug(" rescheduleJob::{},{}", cronTrigger,rescedule);
								log.debug("==============================================================");
							}

						}
					} catch (SchedulerException e) {
						log.error("Scheduler add listerner failed::{}", e);
					}
				});
			} catch (SchedulerException e) {
				log.error("Scheduler run faied::{}", e);
			}
		});
	}

}
