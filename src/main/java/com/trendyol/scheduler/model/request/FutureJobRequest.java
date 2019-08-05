package com.trendyol.scheduler.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trendyol.scheduler.model.enums.FutureJobSchedulingType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class FutureJobRequest {

    private FutureJobSchedulingType futureJobSchedulingType;
    private Date startDate;
    private Date expireDate;
    private String name;
    private String application;
    private String path;
    private String method;
    private String payload;

    private FutureJobRequest() {

    }

    private FutureJobRequest(FutureJobSchedulingType futureJobSchedulingType) {
        this.futureJobSchedulingType = futureJobSchedulingType;
    }

    public static FutureJobRequest aSingleFutureJobRequest() {
        return new FutureJobRequest(FutureJobSchedulingType.SINGLE);
    }

    public static FutureJobRequest aMultipleFutureJobRequest() {
        return new FutureJobRequest(FutureJobSchedulingType.MULTIPLE);
    }

    @JsonIgnore
    public String getRequestUniqueKey() {
        return DigestUtils.md5Hex(futureJobSchedulingType.name() + name + application + path + method + payload);
    }

    @JsonIgnore
    public boolean isSingleSchedulingType() {
        return futureJobSchedulingType == FutureJobSchedulingType.SINGLE;
    }

    public FutureJobSchedulingType getFutureJobSchedulingType() {
        return futureJobSchedulingType;
    }

    public FutureJobRequest futureJobSchedulingType(FutureJobSchedulingType futureJobSchedulingType) {
        this.futureJobSchedulingType = futureJobSchedulingType;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public FutureJobRequest startDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public FutureJobRequest expireDate(Date expireDate) {
        this.expireDate = expireDate;
        return this;
    }

    public String getName() {
        return name;
    }

    public FutureJobRequest name(String name) {
        this.name = name;
        return this;
    }

    public String getApplication() {
        return application;
    }

    public FutureJobRequest application(String application) {
        this.application = application;
        return this;
    }

    public String getPath() {
        return path;
    }

    public FutureJobRequest path(String path) {
        this.path = path;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public FutureJobRequest method(String method) {
        this.method = method;
        return this;
    }

    public String getPayload() {
        return payload;
    }

    public FutureJobRequest payload(String payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("futureJobSchedulingType", futureJobSchedulingType)
                .append("startDate", startDate)
                .append("expireDate", expireDate)
                .append("name", name)
                .append("application", application)
                .append("path", path)
                .append("method", method)
                .append("payload", payload)
                .toString();
    }
}
