package com.trendyol.scheduler.service;

import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJobExecutionHistory;
import com.trendyol.scheduler.utils.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.trendyol.scheduler.domain.enums.JobExecutionStatus.In_Progress;

@Service
public class ScheduledJobExecutorService extends JobExecutorService<ScheduledJob> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledJobExecutorService.class);

    private final ScheduledJobExecutionHistoryService scheduledJobExecutionHistoryService;
    private final RestTemplateScheduledJobExecutorService restTemplateScheduledJobExecutorService;

    @Autowired
    public ScheduledJobExecutorService(
            ScheduledJobExecutionHistoryService scheduledJobExecutionHistoryService,
            RestTemplateScheduledJobExecutorService restTemplateScheduledJobExecutorService,
            JobSynchronizer jobSynchronizer) {
        super(jobSynchronizer);
        this.scheduledJobExecutionHistoryService = scheduledJobExecutionHistoryService;
        this.restTemplateScheduledJobExecutorService = restTemplateScheduledJobExecutorService;
    }

    @Override
    public void process(ScheduledJob scheduledJob) {
        String taskId = UUID.randomUUID().toString();
        LOGGER.debug("Execution of {} started task Id {}", scheduledJob.getName(), taskId);

        ScheduledJobExecutionHistory jobExecutionHistory = new ScheduledJobExecutionHistory();
        jobExecutionHistory.setStartDate(Clock.now().toDate());
        jobExecutionHistory.setId(taskId);
        jobExecutionHistory.setScheduledJob(scheduledJob);
        jobExecutionHistory.setJobExecutionStatus(In_Progress);
        scheduledJobExecutionHistoryService.save(jobExecutionHistory);

        try {
            restTemplateScheduledJobExecutorService.executeWithTaskId(taskId, scheduledJob);
        } catch (Exception e) {
            jobExecutionHistory.fail(e);
            LOGGER.error("Scheduled Job name " + scheduledJob.getName() + " failed task Id " + taskId, e);
            scheduledJobExecutionHistoryService.save(jobExecutionHistory);
        }
        LOGGER.debug("Execution of {} ended task Id {} with status {}", scheduledJob.getName(), taskId, jobExecutionHistory.getJobExecutionStatus());
    }
}
