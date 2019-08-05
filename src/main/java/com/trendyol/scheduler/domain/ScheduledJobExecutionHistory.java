package com.trendyol.scheduler.domain;

import com.trendyol.scheduler.domain.entity.BaseEntity;
import com.trendyol.scheduler.domain.enums.JobExecutionStatus;
import com.trendyol.scheduler.utils.Clock;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

import static com.trendyol.scheduler.domain.enums.JobExecutionStatus.Failed;
import static java.util.Arrays.deepToString;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseStackTrace;

@Entity
@Table(name = "scheduled_job_execution_histories")
public class ScheduledJobExecutionHistory extends BaseEntity {

    private static final long serialVersionUID = 2245140133512406915L;
    private static final int ERROR_DETAIL_MAX_LENGTH = 4096;

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "scheduled_job_id")
    private ScheduledJob scheduledJob;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "job_execution_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobExecutionStatus jobExecutionStatus;

    @Column(name = "error_detail", length = ERROR_DETAIL_MAX_LENGTH)
    private String errorDetail;

    public void fail(Exception exception) {
        setJobExecutionStatus(Failed);
        setEndDate(Clock.now().toDate());
        setErrorDetail(substring(deepToString(getRootCauseStackTrace(exception)), 0, ERROR_DETAIL_MAX_LENGTH));
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ScheduledJob getScheduledJob() {
        return scheduledJob;
    }

    public void setScheduledJob(ScheduledJob scheduledJob) {
        this.scheduledJob = scheduledJob;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public JobExecutionStatus getJobExecutionStatus() {
        return jobExecutionStatus;
    }

    public void setJobExecutionStatus(JobExecutionStatus jobExecutionStatus) {
        this.jobExecutionStatus = jobExecutionStatus;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ScheduledJobExecutionHistory)) {
            return false;
        }

        ScheduledJobExecutionHistory that = (ScheduledJobExecutionHistory) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getId(), that.getId())
                .append(getScheduledJob(), that.getScheduledJob())
                .append(getStartDate(), that.getStartDate())
                .append(getEndDate(), that.getEndDate())
                .append(getJobExecutionStatus(), that.getJobExecutionStatus())
                .append(getErrorDetail(), that.getErrorDetail())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getId())
                .append(getScheduledJob())
                .append(getStartDate())
                .append(getEndDate())
                .append(getJobExecutionStatus())
                .append(getErrorDetail())
                .toHashCode();
    }
}
