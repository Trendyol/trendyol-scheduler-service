package com.trendyol.scheduler.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CouchbaseConfigurationPropertiesTest.TestConfiguration.class)
public class CouchbaseConfigurationPropertiesTest {

    @Autowired
    private CouchbaseConfigurationProperties couchbaseConfigurationProperties;

    @MockBean
    private CouchbaseProperties couchbaseProperties;

    @Test
    public void it_should_get_couchbase_configuration_properties() {
        assertThat(couchbaseConfigurationProperties.getCouchbaseProperties()).isEqualTo(couchbaseProperties);
        assertThat(couchbaseConfigurationProperties.getPassword()).isNull();
        assertThat(couchbaseConfigurationProperties.getSchedulerBucket()).isEqualTo("Scheduler");
        assertThat(couchbaseConfigurationProperties.getSchedulerBucketPassword()).isEqualTo("password");
    }

    @EnableConfigurationProperties(CouchbaseConfigurationProperties.class)
    public static class TestConfiguration {
    }
}