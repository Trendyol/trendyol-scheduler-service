package com.trendyol.scheduler.service;

import com.couchbase.client.java.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "scheduler-service.synchronizer.type", havingValue = "couchbase")
public class JobSynchronizeCouchbaseService implements JobSynchronizer {

    private static final long INITIAL_COUNTER_VALUE = 0L;
    private static final int COUNTER_DELTA_VALUE = 1;

    private final Bucket schedulerBucket;

    @Autowired
    public JobSynchronizeCouchbaseService(Bucket schedulerBucket) {
        this.schedulerBucket = schedulerBucket;
    }

    public boolean isAssignableToThisExecution(SyncJob job) {
        return getCounter(job) == INITIAL_COUNTER_VALUE;
    }

    private Long getCounter(SyncJob job) {
        return schedulerBucket.counter(job.jobKey(), COUNTER_DELTA_VALUE, INITIAL_COUNTER_VALUE, job.jobTTL()).content();
    }
}

