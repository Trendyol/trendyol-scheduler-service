package com.trendyol.scheduler.service;

import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.repository.ScheduledJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ScheduledJobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledJobService.class);

    private final ScheduledJobRepository scheduledJobRepository;

    private final TaskScheduler taskScheduler;

    private final ScheduledJobExecutorService scheduledJobExecutorService;

    @Autowired
    public ScheduledJobService(ScheduledJobRepository scheduledJobRepository,
                               TaskScheduler taskScheduler,
                               ScheduledJobExecutorService scheduledJobExecutorService) {
        this.scheduledJobRepository = scheduledJobRepository;
        this.taskScheduler = taskScheduler;
        this.scheduledJobExecutorService = scheduledJobExecutorService;
    }

    @PostConstruct
    public void initialize() {
        scheduledJobRepository.findAllByActive(true).forEach(eachJob -> {
            taskScheduler.schedule(() -> scheduledJobExecutorService.execute(eachJob), new CronTrigger(eachJob.getCronExpression()));
            LOGGER.debug("Job named: {} is registered for scheduling with cron : {}", eachJob.getName(), eachJob.getCronExpression());
        });
    }

    public void save(ScheduledJob scheduledJob) {
        scheduledJobRepository.save(scheduledJob);
        taskScheduler.schedule(() -> scheduledJobExecutorService.execute(scheduledJob), new CronTrigger(scheduledJob.getCronExpression()));
        LOGGER.debug("Job named: {} is saved and registered for scheduling", scheduledJob.getName());
    }
}
