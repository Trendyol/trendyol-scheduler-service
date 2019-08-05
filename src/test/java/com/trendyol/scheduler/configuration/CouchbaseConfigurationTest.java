package com.trendyol.scheduler.configuration;

import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CouchbaseCluster.class})
public class CouchbaseConfigurationTest {

    @InjectMocks
    private CouchbaseConfiguration couchbaseConfiguration;

    @Mock
    private CouchbaseConfigurationProperties couchbaseConfigurationProperties;

    @Before
    public void setUp() {
        when(couchbaseConfigurationProperties.getPassword()).thenReturn("");
        CouchbaseProperties couchbaseProperties = new CouchbaseProperties();
        couchbaseProperties.setBootstrapHosts(Collections.singletonList("127.0.0.1"));
        when(couchbaseConfigurationProperties.getCouchbaseProperties()).thenReturn(couchbaseProperties);
    }

    @Test
    public void it_should_get_bootstrap_hosts() {
        List<String> bootstrapHosts = couchbaseConfiguration.getBootstrapHosts();

        assertThat(bootstrapHosts).isNotNull();
        assertThat(bootstrapHosts).isNotEmpty();
        assertThat(bootstrapHosts.get(0)).isEqualTo("127.0.0.1");
    }

    @Test
    public void it_should_get_bucket_name() {
        when(couchbaseConfigurationProperties.getSchedulerBucket()).thenReturn("Scheduler");

        String bucketName = couchbaseConfiguration.getBucketName();

        assertThat(bucketName).isNotNull();
        assertThat(bucketName).isEqualTo("Scheduler");
    }

    @Test
    public void it_should_get_bucket_password() {
        when(couchbaseConfigurationProperties.getSchedulerBucketPassword()).thenReturn("password");

        String password = couchbaseConfiguration.getBucketPassword();

        assertThat(password).isNotNull();
        assertThat(password).isEqualTo("password");
    }

    @Test
    public void it_should_get_scheduler_bucket() throws Exception {
        //Given
        PowerMockito.mockStatic(CouchbaseCluster.class);
        CouchbaseCluster couchbaseCluster = Mockito.mock(CouchbaseCluster.class);
        PowerMockito.when(CouchbaseCluster.create(any(CouchbaseEnvironment.class), anyList())).thenReturn(couchbaseCluster);

        when(couchbaseConfigurationProperties.getSchedulerBucket()).thenReturn("Scheduler");
        when(couchbaseConfigurationProperties.getSchedulerBucketPassword()).thenReturn("password");

        //When
        couchbaseConfiguration.schedulerBucket();

        //Than
        verify(couchbaseCluster).openBucket("Scheduler", "password");
    }
}