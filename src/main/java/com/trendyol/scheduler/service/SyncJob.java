package com.trendyol.scheduler.service;

import com.trendyol.scheduler.domain.entity.AuditingEntity;

public abstract class SyncJob extends AuditingEntity {
    private static final long serialVersionUID = 8546464187777917466L;
    protected static final double MAX_PERMITTED_JOB_RUN_PERIOD_COEFFICIENT = 0.8D;

    public abstract String jobKey();

    public abstract int jobTTL();

}
