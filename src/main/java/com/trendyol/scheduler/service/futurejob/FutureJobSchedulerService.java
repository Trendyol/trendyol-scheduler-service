package com.trendyol.scheduler.service.futurejob;

import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.domain.enums.FutureJobStatus;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class FutureJobSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureJobSchedulerService.class);
    private static final int BUFFER_IN_SECONDS = 2;

    private final FutureJobService futureJobService;
    private final TaskScheduler futureJobTaskScheduler;
    private final FutureJobExecutorService futureJobExecutorService;

    public FutureJobSchedulerService(FutureJobService futureJobService,
                                     TaskScheduler futureJobTaskScheduler,
                                     FutureJobExecutorService futureJobExecutorService) {
        this.futureJobService = futureJobService;
        this.futureJobTaskScheduler = futureJobTaskScheduler;
        this.futureJobExecutorService = futureJobExecutorService;
    }

    @PostConstruct
    public void initialize() {
        long scheduledFutureJobCount = futureJobService.getWaitingFutureJobs().stream()
                .peek(this::schedule)
                .count();

        LOGGER.debug("{} Waiting future jobs scheduled successfully.", scheduledFutureJobCount);
    }

    public void schedule(FutureJob futureJob) {
        markIfExpired(futureJob);
        scheduleIfWaiting(futureJob);
    }

    private void markIfExpired(FutureJob futureJob) {
        if (futureJob.isExpired()) {
            futureJob.setFutureJobStatus(FutureJobStatus.EXPIRED);
            futureJobService.save(futureJob);
            LOGGER.debug("Future job marked as EXPIRED on startup due to date: {}", futureJob);
        }
    }

    private void scheduleIfWaiting(FutureJob futureJob) {
        if (futureJob.isWaiting()) {
            futureJobTaskScheduler.schedule(() ->
                    futureJobExecutorService.execute(futureJob), new DateTime(futureJob.getStartTime()).plusSeconds(BUFFER_IN_SECONDS).toDate());
        }
    }
}
