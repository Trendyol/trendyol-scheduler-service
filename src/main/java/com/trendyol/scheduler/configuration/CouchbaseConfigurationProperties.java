package com.trendyol.scheduler.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "couchbase")
public class CouchbaseConfigurationProperties {

    private final CouchbaseProperties couchbaseProperties;
    private String password;
    private String schedulerBucket;
    private String schedulerBucketPassword;

    @Autowired
    public CouchbaseConfigurationProperties(CouchbaseProperties couchbaseProperties) {
        this.couchbaseProperties = couchbaseProperties;
    }

    public CouchbaseProperties getCouchbaseProperties() {
        return couchbaseProperties;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchedulerBucket() {
        return schedulerBucket;
    }

    public void setSchedulerBucket(String schedulerBucket) {
        this.schedulerBucket = schedulerBucket;
    }

    public String getSchedulerBucketPassword() {
        return schedulerBucketPassword;
    }

    public void setSchedulerBucketPassword(String schedulerBucketPassword) {
        this.schedulerBucketPassword = schedulerBucketPassword;
    }
}
