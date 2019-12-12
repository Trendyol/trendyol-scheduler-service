package com.trendyol.scheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JobExecutorService<T extends SyncJob> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobExecutorService.class);

    private final JobSynchronizer jobSynchronizer;

    public JobExecutorService(JobSynchronizer jobSynchronizer) {
        this.jobSynchronizer = jobSynchronizer;
    }

    public final void execute(T job) {
        LOGGER.debug("Sync job starting with parameters: {}", job);

        if (isNotAssignableToCurrentInstance(job)) {
            LOGGER.warn("This Task is assigned to other instance. Sync Job: {}", job);
            return;
        }

        process(job);
    }

    final boolean isNotAssignableToCurrentInstance(T job) {
        return !jobSynchronizer.isAssignableToThisExecution(job);
    }

    public abstract void process(T job);
}
