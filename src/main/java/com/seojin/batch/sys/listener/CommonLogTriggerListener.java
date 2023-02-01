package com.seojin.batch.sys.listener;

import com.seojin.batch.biz.scheduler.TriggerExecution;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.listeners.TriggerListenerSupport;

import com.seojin.batch.biz.scheduler.TriggerExecution;
import com.seojin.batch.biz.scheduler.service.SchedulerService;

import lombok.extern.slf4j.Slf4j;

/**
 * CommonLogTriggerListener
 */
@Slf4j
public class CommonLogTriggerListener extends TriggerListenerSupport {

	/**
	 * LISTENER_POSTFIX
	 */
	private static final String LISTENER_POSTFIX = "_commonLogTriggerListener";

	/**
	 * LISTENER NAME
	 */
	private final String name;

	/**
	 * schedulerService
	 */
	private final SchedulerService schedulerService;

	/**
	 * CommonLogTriggerListener init
	 */
	public CommonLogTriggerListener(String key, SchedulerService schedulerService) {
		super();

		this.name = key + LISTENER_POSTFIX;
		this.schedulerService = schedulerService;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Trigger가 실행된 상태
	 * 리스너 중에서 가장 먼저 실행됨
	 */
	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		super.triggerFired(trigger, context);
	}

	/**
	 * Trigger가 완료된 상태
	 */
	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
		try {
			TriggerExecution triggerExecution = (TriggerExecution) context.getResult();
			schedulerService.updateTriggerExecution(triggerExecution);
		} catch (SchedulerException e) {
			log.error("update trigger execution log failed", e);
		}

		super.triggerComplete(trigger, context, triggerInstructionCode);
	}

	/**
	 * Trigger 중단 여부를 확인하는 메소드
	 * Job을 수행하기 전 상태
	 * 
	 * 반환값이 false인 경우, Job 수행
	 * 반환값이 true인 경우, Job을 수행하지않고 'SchedulerListtener.jobExecutionVetoed'로 넘어감
	 */
	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		return super.vetoJobExecution(trigger, context);
	}

	/**
	 * Trigger가 중단된 상태
	 */
	@Override
	public void triggerMisfired(Trigger trigger) {
		super.triggerMisfired(trigger);
	}
}
