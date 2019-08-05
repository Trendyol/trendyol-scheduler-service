package com.trendyol.scheduler.domain;

import com.trendyol.scheduler.utils.DateUtils;

public class InternalScheduledJob extends ScheduledJob {

    private static final long serialVersionUID = 2166734287661476999L;

    public InternalScheduledJob(String name, String cronExpression) {
        setId(name.hashCode());
        setActive(true);
        setApplication("scheduler-service");
        setCronExpression(cronExpression);
        setName(name);
        setCreatedDate(DateUtils.toDateTime("01/01/1970 11:12").toDate());
        setLastModifiedDate(DateUtils.toDateTime("01/01/1970 11:12").toDate());
    }
}
