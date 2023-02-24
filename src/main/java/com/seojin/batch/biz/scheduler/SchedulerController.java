package com.seojin.batch.biz.scheduler;

import com.seojin.batch.biz.scheduler.service.SchedulerService;
import com.seojin.commons.grid.Grid;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.seojin.commons.base.BaseRestController;
import com.seojin.commons.grid.Grid;
import com.seojin.batch.biz.scheduler.service.SchedulerService;

/**
 * Description : Scheduler Controller
 * <p>
 *
 * <pre>
 * Note
 * -  
 * </pre>
 *
 */
@RestController
@RequestMapping("api/v1.0/schedule")
public class SchedulerController extends BaseRestController {

	/**
	 * SchedulerService
	 */
	private SchedulerService schedulerService;

	@Autowired
	public void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	/**
	 * Description : Schedules List 검색
	 * <p>
	 * @param scheduleParam
	 * @return
	 * @throws SchedulerException
	 */
	@GetMapping
	public Grid findScheduleList(ScheduleParam scheduleParam) throws SchedulerException {
		return schedulerService.findScheduleList(scheduleParam);
	}

	/**
	 * Description : Schedule detail
	 * <p>
	 * @param jobName
	 * @param jobGroup
	 * @return
	 * @throws SchedulerException
	 */
	@GetMapping("/{jobGroup}/{jobName}")
	public ScheduleCronTrigger findSchedule(@PathVariable("jobGroup") String jobGroup, @PathVariable("jobName") String jobName) throws SchedulerException {
		ScheduleParam scheduleParam = new ScheduleParam();
		scheduleParam.setJobGroup(jobGroup);
		scheduleParam.setJobName(jobName);

		return schedulerService.findSchedule(scheduleParam);
	}

	/**
	 * Description : Schedule insert
	 * <p>
	 * @param scheduleParam
	 * @throws SchedulerException
	 */
	@PostMapping
	public void inputSchedule(@RequestBody ScheduleParam scheduleParam) throws SchedulerException {
		schedulerService.inputSchedule(scheduleParam);
	}

	/**
	 * Description : Schedule update
	 * <p>
	 * @param param
	 * @throws SchedulerException
	 */
	@PutMapping("/{jobGroup}/{jobName}")
	public void modifySchedule(@RequestBody ScheduleParam scheduleParam) throws SchedulerException {
		schedulerService.modifySchedule(scheduleParam);
	}

	/**
	 * Description : Schedule resume
	 * <p>
	 * @param scheduleParam
	 * @throws SchedulerException
	 */
	@PutMapping("/{jobGroup}/{jobName}/resume")
	public void setScheduleResume(ScheduleParam scheduleParam) throws SchedulerException {
		schedulerService.setScheduleResume(scheduleParam);
	}

	/**
	 * Description : Schedule Pause
	 * <p>
	 * @param scheduleParam
	 * @throws SchedulerException
	 */
	@PutMapping("/{jobGroup}/{jobName}/pause")
	public void setSchedulePause(ScheduleParam scheduleParam) throws SchedulerException {
		schedulerService.setSchedulePause(scheduleParam);
	}

	/**
	 * Description : Schedule execute
	 * <p>
	 * @param scheduleParam
	 * @throws SchedulerException
	 */
	@PostMapping("/simple")
	public void inputSimpleSchedule(@RequestBody ScheduleParam scheduleParam) throws SchedulerException {
		schedulerService.inputSimpleSchedule(scheduleParam);
	}

	/**
	 * Description : Schedule unused
	 * <p>
	 * @param scheduleParam
	 * @throws SchedulerException
	 */
	@DeleteMapping
	public void deleteSchedule(@RequestBody ScheduleParam scheduleParam) throws SchedulerException {
		schedulerService.deleteSchedule(scheduleParam);
	}

	/**
	 * Description : schedule log list
	 * <p>
	 * @param scheduleParam
	 * @return
	 * @throws SchedulerException
	 */
	@GetMapping("/{jobGroup}/{jobName}/schedulelog")
	public Grid findScheduleLogList(ScheduleParam scheduleParam) throws SchedulerException {
		return schedulerService.findScheduleLogList(scheduleParam);
	}

	/**
	 * Description : batch job log list
	 * <p>
	 * @param batchParam
	 * @return
	 * @throws BatchJobException
	 */
	@GetMapping("/{jobName}/batchjoblog/{jobExecutionId}")
	public Grid findBatchJobLog(ScheduleParam scheduleParam) throws SchedulerException {
		return schedulerService.findBatchJobLog(scheduleParam);
	}

	/**
	 * Description : batch step log list
	 * <p>
	 * @param batchParam
	 * @return
	 * @throws BatchJobException
	 */
	@GetMapping("/batchsteplog/{jobExecutionId}")
	public Grid findBatchStepLogList(ScheduleParam scheduleParam) throws SchedulerException {
		return schedulerService.findBatchStepLogList(scheduleParam);
	}

}
