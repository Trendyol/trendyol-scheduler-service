package com.trendyol.scheduler.configuration;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

import java.util.List;

@Configuration
@EnableCouchbaseRepositories
@Profile("!unit-test")
public class CouchbaseConfiguration extends AbstractCouchbaseConfiguration {

    private final CouchbaseConfigurationProperties couchbaseConfigurationProperties;

    @Autowired
    public CouchbaseConfiguration(CouchbaseConfigurationProperties couchbaseConfigurationProperties) {
        this.couchbaseConfigurationProperties = couchbaseConfigurationProperties;
    }

    @Bean
    public Bucket schedulerBucket() throws Exception {
        return couchbaseCluster().openBucket(
                couchbaseConfigurationProperties.getSchedulerBucket(),
                couchbaseConfigurationProperties.getSchedulerBucketPassword());
    }

    @Override
    protected CouchbaseEnvironment getEnvironment() {
        return DefaultCouchbaseEnvironment.builder().connectTimeout(30000).kvTimeout(10000).build();
    }

    @Override
    protected List<String> getBootstrapHosts() {
        return couchbaseConfigurationProperties.getCouchbaseProperties().getBootstrapHosts();
    }

    @Override
    protected String getBucketName() {
        return couchbaseConfigurationProperties.getSchedulerBucket();
    }

    @Override
    protected String getBucketPassword() {
        return couchbaseConfigurationProperties.getSchedulerBucketPassword();
    }
}
