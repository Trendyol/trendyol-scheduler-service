package com.trendyol.scheduler.builder;

import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.domain.enums.FutureJobStatus;

import java.util.Date;

public final class FutureJobBuilder {

    private Long id;
    private String taskId;
    private Date startTime;
    private Date expireTime;
    private String name;
    private FutureJobStatus futureJobStatus = FutureJobStatus.WAITING;
    private String application;
    private String path;
    private String method;
    private String payload;
    private String hashKey;
    private String url;

    private FutureJobBuilder() {
    }

    public static FutureJobBuilder aFutureJob() {
        return new FutureJobBuilder();
    }

    public FutureJobBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public FutureJobBuilder taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public FutureJobBuilder startTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public FutureJobBuilder expireTime(Date expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    public FutureJobBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FutureJobBuilder futureJobStatus(FutureJobStatus futureJobStatus) {
        this.futureJobStatus = futureJobStatus;
        return this;
    }

    public FutureJobBuilder application(String application) {
        this.application = application;
        return this;
    }

    public FutureJobBuilder path(String path) {
        this.path = path;
        return this;
    }

    public FutureJobBuilder method(String method) {
        this.method = method;
        return this;
    }

    public FutureJobBuilder payload(String payload) {
        this.payload = payload;
        return this;
    }

    public FutureJobBuilder hashKey(String hashKey) {
        this.hashKey = hashKey;
        return this;
    }

    public FutureJobBuilder url(String url) {
        this.url = url;
        return this;
    }

    public FutureJob build() {
        FutureJob futureJob = new FutureJob();
        futureJob.setId(id);
        futureJob.setTaskId(taskId);
        futureJob.setStartTime(startTime);
        futureJob.setExpireTime(expireTime);
        futureJob.setName(name);
        futureJob.setFutureJobStatus(futureJobStatus);
        futureJob.setApplication(application);
        futureJob.setPath(path);
        futureJob.setMethod(method);
        futureJob.setPayload(payload);
        futureJob.setHashKey(hashKey);
        futureJob.setUrl(url);
        return futureJob;
    }
}
