package com.trendyol.scheduler.builder.domain;

import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJobExecutionHistory;
import com.trendyol.scheduler.domain.enums.JobExecutionStatus;

import java.util.Date;

public final class ScheduledJobExecutionHistoryBuilder {
    private String id;
    private ScheduledJob scheduledJob;
    private Date startDate;
    private Date endDate;
    private JobExecutionStatus jobExecutionStatus;

    private ScheduledJobExecutionHistoryBuilder() {
    }

    public static ScheduledJobExecutionHistoryBuilder aScheduledJobExecutionHistory() {
        return new ScheduledJobExecutionHistoryBuilder();
    }

    public ScheduledJobExecutionHistoryBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ScheduledJobExecutionHistoryBuilder scheduledJob(ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
        return this;
    }

    public ScheduledJobExecutionHistoryBuilder startDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public ScheduledJobExecutionHistoryBuilder endDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public ScheduledJobExecutionHistoryBuilder jobExecutionStatus(JobExecutionStatus jobExecutionStatus) {
        this.jobExecutionStatus = jobExecutionStatus;
        return this;
    }

    public ScheduledJobExecutionHistory build() {
        ScheduledJobExecutionHistory scheduledJobExecutionHistory = new ScheduledJobExecutionHistory();
        scheduledJobExecutionHistory.setId(id);
        scheduledJobExecutionHistory.setScheduledJob(scheduledJob);
        scheduledJobExecutionHistory.setStartDate(startDate);
        scheduledJobExecutionHistory.setEndDate(endDate);
        scheduledJobExecutionHistory.setJobExecutionStatus(jobExecutionStatus);
        return scheduledJobExecutionHistory;
    }
}
