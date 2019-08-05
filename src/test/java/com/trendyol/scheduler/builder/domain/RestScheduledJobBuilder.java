package com.trendyol.scheduler.builder.domain;

import com.trendyol.scheduler.domain.RestScheduledJob;

public final class RestScheduledJobBuilder {

    private Integer id;
    private String name;
    private String application;
    private String cronExpression;
    private boolean active;
    private String path;
    private String method;
    private String payload;
    private String url;

    private RestScheduledJobBuilder() {
    }

    public static RestScheduledJobBuilder aRestScheduledJob() {
        return new RestScheduledJobBuilder();
    }

    public RestScheduledJobBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public RestScheduledJobBuilder name(String name) {
        this.name = name;
        return this;
    }

    public RestScheduledJobBuilder application(String application) {
        this.application = application;
        return this;
    }

    public RestScheduledJobBuilder cronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
        return this;
    }

    public RestScheduledJobBuilder active(boolean active) {
        this.active = active;
        return this;
    }


    public RestScheduledJobBuilder path(String path) {
        this.path = path;
        return this;
    }

    public RestScheduledJobBuilder method(String method) {
        this.method = method;
        return this;
    }

    public RestScheduledJobBuilder payload(String payload) {
        this.payload = payload;
        return this;
    }

    public RestScheduledJobBuilder url(String url) {
        this.url = url;
        return this;
    }

    public RestScheduledJob build() {
        RestScheduledJob restScheduledJob = new RestScheduledJob();
        restScheduledJob.setPath(path);
        restScheduledJob.setMethod(method);
        restScheduledJob.setPayload(payload);
        restScheduledJob.setId(id);
        restScheduledJob.setName(name);
        restScheduledJob.setApplication(application);
        restScheduledJob.setCronExpression(cronExpression);
        restScheduledJob.setActive(active);
        restScheduledJob.setUrl(url);
        return restScheduledJob;
    }

}
