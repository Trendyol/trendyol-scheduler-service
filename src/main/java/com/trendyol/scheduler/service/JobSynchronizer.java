package com.trendyol.scheduler.service;

/**
 * @author dilaverd
 * @since 10.12.2019
 */
public interface JobSynchronizer {
    boolean isAssignableToThisExecution(SyncJob job);
}
