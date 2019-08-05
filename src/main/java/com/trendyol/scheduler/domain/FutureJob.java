package com.trendyol.scheduler.domain;

import com.trendyol.scheduler.domain.enums.FutureJobStatus;
import com.trendyol.scheduler.service.SyncJob;
import com.trendyol.scheduler.utils.Clock;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.trendyol.scheduler.domain.enums.FutureJobStatus.EXPIRED;
import static com.trendyol.scheduler.domain.enums.FutureJobStatus.WAITING;
import static org.joda.time.Seconds.secondsBetween;

@Entity
@Table(name = "future_jobs")
@SequenceGenerator(name = "seq_future_jobs", sequenceName = "seq_future_jobs")
public class FutureJob extends SyncJob {

    private static final long serialVersionUID = 6321672762347734578L;
    private static final String FUTURE_JOB_PREFIX = "FutureJob_";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_future_jobs")
    private Long id;

    @Column(name = "task_id", length = 36)
    private String taskId;

    @Column(name = "hash_key", nullable = false, length = 32)
    private String hashKey;

    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "expire_time")
    private Date expireTime;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "future_job_status", nullable = false, length = 100)
    @Enumerated(value = EnumType.STRING)
    private FutureJobStatus futureJobStatus = FutureJobStatus.WAITING;

    @Column(name = "application", nullable = false, length = 100)
    private String application;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "method", nullable = false, length = 10)
    private String method;

    @Column(name = "payload", length = 1024)
    private String payload;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    public boolean isExpired() {
        return EXPIRED == futureJobStatus || (Objects.nonNull(expireTime) && Clock.now().toDate().after(expireTime));
    }

    public boolean isWaiting() {
        return WAITING == futureJobStatus;
    }

    public boolean isNotWaiting() {
        return !isWaiting();
    }

    @Override
    public String jobKey() {
        return FUTURE_JOB_PREFIX + id;
    }

    @Override
    public int jobTTL() {
        return Optional.ofNullable(expireTime)
                .map(this::getSeconds)
                .orElse(getSeconds(Clock.toDateTime(startTime).plusHours(1).toDate()));
    }

    private int getSeconds(Date date) {
        return secondsBetween(Clock.now(), Clock.toDateTime(date)).getSeconds();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FutureJobStatus getFutureJobStatus() {
        return futureJobStatus;
    }

    public void setFutureJobStatus(FutureJobStatus futureJobStatus) {
        this.futureJobStatus = futureJobStatus;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("taskId", taskId)
                .append("name", name)
                .append("application", application)
                .append("url", url)
                .toString();
    }
}
