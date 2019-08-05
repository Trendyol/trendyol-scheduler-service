package com.trendyol.scheduler.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@DiscriminatorValue("bean_scheduled_job")
@Table(name = "bean_scheduled_jobs")
public class BeanScheduledJob extends ScheduledJob {

    private static final long serialVersionUID = -2543868153535902971L;

    @Column(name = "bean_name", nullable = false)
    private String beanName;

    @Column(name = "method_name", nullable = false)
    private String methodName;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
