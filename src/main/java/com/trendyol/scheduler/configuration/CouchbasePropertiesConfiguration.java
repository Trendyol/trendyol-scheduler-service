package com.trendyol.scheduler.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author dilaverd
 * @since 12.12.2019
 */
@Configuration
@Profile("!unit-test")
@ConditionalOnProperty(name = "scheduler-service.synchronizer.type", havingValue = "couchbase")
public class CouchbasePropertiesConfiguration {
    @Bean
    public CouchbaseProperties couchbaseProperties() {
        return new CouchbaseProperties();
    }
}
