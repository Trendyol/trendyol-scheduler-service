package com.trendyol.scheduler.jobs;

import com.trendyol.scheduler.domain.InternalScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJobExecutionHistory;
import com.trendyol.scheduler.domain.enums.JobExecutionStatus;
import com.trendyol.scheduler.service.ScheduledJobExecutionHistoryService;
import com.trendyol.scheduler.utils.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleJobStateCheckJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobStateCheckJob.class);

    private static final String CRON_STRING_FOR_EVERY_DAY = "0 0 0 */1 * *";

    private final InternalJobExecutorTemplate internalJobExecutorTemplate;
    private final ScheduledJobExecutionHistoryService scheduledJobExecutionHistoryService;

    private final ScheduledJob jobDefinition = new InternalScheduledJob(
            "Scheduled Job Controller in Stuck In_Progress Status",
            CRON_STRING_FOR_EVERY_DAY
    );

    @Autowired
    public ScheduleJobStateCheckJob(InternalJobExecutorTemplate internalJobExecutorTemplate,
                                    ScheduledJobExecutionHistoryService scheduledJobExecutionHistoryService) {
        this.internalJobExecutorTemplate = internalJobExecutorTemplate;
        this.scheduledJobExecutionHistoryService = scheduledJobExecutionHistoryService;
    }

    @Scheduled(cron = CRON_STRING_FOR_EVERY_DAY)
    public void checkStuckInProgressJobs() {
        internalJobExecutorTemplate.run(jobDefinition, job());
    }

    private Runnable job() {
        return () -> {
            List<ScheduledJobExecutionHistory> staleInProgressScheduledJobs = scheduledJobExecutionHistoryService.getStaleInProgressScheduledJobs();
            LOGGER.debug("Found '{}' stale jobs at In_Progress status", staleInProgressScheduledJobs.size());
            staleInProgressScheduledJobs.forEach(this::makeStaleJobFailed);
        };
    }

    private void makeStaleJobFailed(ScheduledJobExecutionHistory staleJob) {
        staleJob.setJobExecutionStatus(JobExecutionStatus.Failed);
        staleJob.setEndDate(Clock.now().toDate());
        staleJob.setErrorDetail("Failed due for being stale by ScheduleJobStateCheckJob");
        scheduledJobExecutionHistoryService.save(staleJob);
    }
}
