package com.trendyol.scheduler.model;

public class SchedulerServiceException extends RuntimeException {

    public SchedulerServiceException() {
    }

    public SchedulerServiceException(String message) {
        super(message);
    }

    public SchedulerServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchedulerServiceException(Throwable cause) {
        super(cause);
    }

    public SchedulerServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
