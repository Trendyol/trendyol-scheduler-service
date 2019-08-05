package com.trendyol.scheduler.domain.entity;

import com.trendyol.scheduler.utils.Clock;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Objects;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class AuditingEntity extends BaseEntity {

    private static final long serialVersionUID = -7599645917579815540L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false)
    private Date createdDate = Clock.now().toDate();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date", nullable = false)
    private Date lastModifiedDate = Clock.now().toDate();

    public Date getCreatedDate() {
        return new Date(this.createdDate.getTime());
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = Objects.nonNull(createdDate) ? new Date(createdDate.getTime()) : null;
    }

    public Date getLastModifiedDate() {
        return new Date(this.lastModifiedDate.getTime());
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = Objects.nonNull(lastModifiedDate) ? new Date(lastModifiedDate.getTime()) : null;
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = Clock.now().toDate();
    }

}
