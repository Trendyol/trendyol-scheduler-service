package com.trendyol.scheduler.domain;

import com.trendyol.scheduler.service.SyncJob;
import com.trendyol.scheduler.utils.Clock;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.springframework.scheduling.support.CronSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.concurrent.TimeUnit;

import static org.joda.time.Seconds.secondsBetween;


@Entity
@Table(name = "scheduled_jobs")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "job_type")
@SequenceGenerator(name = "seq_scheduled_jobs", sequenceName = "seq_scheduled_jobs")
public abstract class ScheduledJob extends SyncJob {

    private static final long serialVersionUID = 8556018559512680304L;
    private static final String SCHEDULED_JOB_PREFIX = "ScheduledJob_";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_scheduled_jobs")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cron_expression", nullable = false)
    private String cronExpression;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "application", nullable = false)
    private String application;

    @Column(name = "url", nullable = false)
    private String url;

    @Override
    public String jobKey() {
        return SCHEDULED_JOB_PREFIX + id;
    }

    @Override
    public int jobTTL() {
        DateTime now = Clock.now();

        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpression);
        DateTime nextCronSequenceDateTime = new DateTime(cronSequenceGenerator.next(now.toDate()));
        Double nextExecutionInSeconds = secondsBetween(now, nextCronSequenceDateTime).getSeconds() * MAX_PERMITTED_JOB_RUN_PERIOD_COEFFICIENT;

        if (nextExecutionInSeconds.intValue() == 0) {
            return (int) TimeUnit.MINUTES.toSeconds(1);
        } else {
            return nextExecutionInSeconds.intValue();
        }
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("application", application)
                .toString();
    }
}
