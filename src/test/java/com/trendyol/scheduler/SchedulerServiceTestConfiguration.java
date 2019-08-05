package com.trendyol.scheduler;

import com.couchbase.client.java.Bucket;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.core.CouchbaseTemplate;

import static org.mockito.Mockito.mock;

@Configuration
public class SchedulerServiceTestConfiguration {

    @Bean
    public CouchbaseProperties couchbaseProperties() {
        return mock(CouchbaseProperties.class);
    }

    @Bean
    public CouchbaseTemplate batchRequestTemplate() {
        return mock(CouchbaseTemplate.class);
    }

    @Bean
    public Bucket schedulerBucket() {
        return mock(Bucket.class);
    }
}