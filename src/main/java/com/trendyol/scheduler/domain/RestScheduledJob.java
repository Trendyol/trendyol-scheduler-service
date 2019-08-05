package com.trendyol.scheduler.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@DiscriminatorValue("rest_scheduled_job")
@Table(name = "rest_scheduled_jobs")
public class RestScheduledJob extends ScheduledJob {

    private static final long serialVersionUID = 5639481725539019686L;

    @Column(name = "path")
    private String path;

    @Column(name = "method")
    private String method;

    @Column(name = "payload")
    private String payload;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
