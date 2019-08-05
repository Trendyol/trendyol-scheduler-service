package com.trendyol.scheduler.builder.domain;

import com.trendyol.scheduler.domain.BeanScheduledJob;

public final class BeanScheduledJobBuilder {

    private Integer id;
    private String name;
    private String application;
    private String cronExpression;
    private boolean active;
    private String beanName;
    private String methodName;
    private String url;

    private BeanScheduledJobBuilder() {
    }

    public static BeanScheduledJobBuilder aBeanScheduledJob() {
        return new BeanScheduledJobBuilder();
    }

    public BeanScheduledJobBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public BeanScheduledJobBuilder name(String name) {
        this.name = name;
        return this;
    }

    public BeanScheduledJobBuilder application(String application) {
        this.application = application;
        return this;
    }

    public BeanScheduledJobBuilder cronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
        return this;
    }

    public BeanScheduledJobBuilder active(boolean active) {
        this.active = active;
        return this;
    }

    public BeanScheduledJobBuilder beanName(String beanName) {
        this.beanName = beanName;
        return this;
    }

    public BeanScheduledJobBuilder methodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public BeanScheduledJobBuilder url(String url) {
        this.url = url;
        return this;
    }

    public BeanScheduledJob build() {
        BeanScheduledJob beanScheduledJob = new BeanScheduledJob();
        beanScheduledJob.setId(id);
        beanScheduledJob.setName(name);
        beanScheduledJob.setApplication(application);
        beanScheduledJob.setBeanName(beanName);
        beanScheduledJob.setMethodName(methodName);
        beanScheduledJob.setCronExpression(cronExpression);
        beanScheduledJob.setActive(active);
        beanScheduledJob.setUrl(url);
        return beanScheduledJob;
    }
}
