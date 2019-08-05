package com.trendyol.scheduler.domain;

import com.trendyol.scheduler.builder.domain.RestScheduledJobBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduledJobTest {

    @Test
    public void it_should_get_id_is_active_and_application_values_correctly() {
        //Given
        ScheduledJob scheduledJob = RestScheduledJobBuilder.aRestScheduledJob().id(1).active(true).application("application").build();

        //When
        int id = scheduledJob.getId();
        String application = scheduledJob.getApplication();
        boolean isActive = scheduledJob.isActive();

        //Then
        assertThat(id).isEqualTo(1);
        assertThat(application).isEqualTo("application");
        assertThat(isActive).isTrue();
    }

    @Test
    public void it_should_convert_to_string_properly() {
        //Given
        ScheduledJob scheduledJob = RestScheduledJobBuilder
                .aRestScheduledJob()
                .id(1)
                .name("name")
                .cronExpression("cron-expression")
                .active(true)
                .application("application")
                .url("url")
                .build();

        //When
        String scheduledJobStringValue = scheduledJob.toString();

        //Then
        assertThat(scheduledJobStringValue).isEqualTo("RestScheduledJob[name=name,application=application]");
    }
}