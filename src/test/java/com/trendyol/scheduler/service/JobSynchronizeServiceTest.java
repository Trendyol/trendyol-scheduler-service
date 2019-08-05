package com.trendyol.scheduler.service;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonLongDocument;
import com.trendyol.scheduler.FrozenClock;
import com.trendyol.scheduler.builder.domain.RestScheduledJobBuilder;
import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.utils.Clock;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.trendyol.scheduler.builder.FutureJobBuilder.aFutureJob;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobSynchronizeServiceTest {

    @Rule
    public FrozenClock.Rule frozenClockRule = new FrozenClock.Rule();

    @InjectMocks
    private JobSynchronizeService jobSynchronizeService;

    @Mock
    private Bucket schedulerBucket;

    @Test
    public void it_should_return_true_when_scheduled_job_is_not_assigned_to_other_instances() {
        //Given
        Clock.freeze(DateTime.parse("2018-04-08T00:00:00"));
        ScheduledJob scheduledJob = RestScheduledJobBuilder.aRestScheduledJob().id(1).cronExpression("0 0 0/1 * * ?").build();

        when(schedulerBucket.counter("ScheduledJob_1", 1, 0, 2880)).thenReturn(JsonLongDocument.create("ScheduledJob_1", 0L));

        //When
        boolean result = jobSynchronizeService.isAssignableToThisExecution(scheduledJob);

        //Then
        assertThat(result).isTrue();

        verify(schedulerBucket).counter("ScheduledJob_1", 1, 0, 2880);

        Clock.unfreeze();
    }

    @Test
    public void it_should_return_false_when_scheduled_job_is_assigned_to_other_instance() {
        //Given
        Clock.freeze(DateTime.parse("2018-04-08T00:00:00"));
        ScheduledJob scheduledJob = RestScheduledJobBuilder.aRestScheduledJob().id(1).cronExpression("0 0 0/1 * * ?").build();

        when(schedulerBucket.counter("ScheduledJob_1", 1, 0, 2880)).thenReturn(JsonLongDocument.create("ScheduledJob_1", 1L));

        //When
        boolean result = jobSynchronizeService.isAssignableToThisExecution(scheduledJob);

        //Then
        assertThat(result).isFalse();

        verify(schedulerBucket).counter("ScheduledJob_1", 1, 0, 2880);

        Clock.unfreeze();
    }

    @Test
    public void it_should_return_true_when_scheduled_job_cron_is_consisted_from_6_digits_for_not_already_assigned_to_other_instance() {
        //Given
        Clock.freeze(DateTime.parse("2018-04-08T00:00:00"));
        ScheduledJob scheduledJob = RestScheduledJobBuilder.aRestScheduledJob().id(1).cronExpression("0 0 * * * *").build();

        when(schedulerBucket.counter("ScheduledJob_1", 1, 0, 2880)).thenReturn(JsonLongDocument.create("ScheduledJob_1", 0L));

        //When
        boolean result = jobSynchronizeService.isAssignableToThisExecution(scheduledJob);

        //Then
        assertThat(result).isTrue();

        verify(schedulerBucket).counter("ScheduledJob_1", 1, 0, 2880);

        Clock.unfreeze();
    }

    @Test
    public void it_should_return_true_with_ONE_MINUTE_TTL_counter_when_scheduled_job_next_execution_time_is_after_0_seconds() {
        //Given
        Clock.freeze(DateTime.parse("2018-04-08T00:00:09"));
        ScheduledJob scheduledJob = RestScheduledJobBuilder.aRestScheduledJob().id(1).cronExpression("*/5 * * * * *").build();

        when(schedulerBucket.counter("ScheduledJob_1", 1, 0, 60)).thenReturn(JsonLongDocument.create("ScheduledJob_1", 0L));

        //When
        boolean result = jobSynchronizeService.isAssignableToThisExecution(scheduledJob);

        //Then
        assertThat(result).isTrue();

        verify(schedulerBucket).counter("ScheduledJob_1", 1, 0, 60);

        Clock.unfreeze();
    }

    @Test
    @FrozenClock(value = "18/10/2018 14:30:40.555", format = "dd/MM/yyyy HH:mm:ss.SSS")
    public void it_should_return_true_when_future_job_is_not_assigned_to_other_instances() {
        //given
        FutureJob futureJob = aFutureJob()
                .id(123L)
                .startTime(Clock.now().toDate())
                .expireTime(Clock.now().plusHours(2).toDate())
                .build();

        when(schedulerBucket.counter("FutureJob_123", 1, 0, 7200)).thenReturn(JsonLongDocument.create("FutureJob_123", 0L));

        //when
        boolean result = jobSynchronizeService.isAssignableToThisExecution(futureJob);

        //then
        assertThat(result).isTrue();
        verify(schedulerBucket).counter("FutureJob_123", 1, 0, 7200);
        verifyNoMoreInteractions(schedulerBucket);
    }

    @Test
    @FrozenClock(value = "18/10/2018 14:30:40.555", format = "dd/MM/yyyy HH:mm:ss.SSS")
    public void it_should_return_true_when_future_job_is_not_assigned_to_other_instances_with_default_expire_date() {
        //given
        FutureJob futureJob = aFutureJob()
                .id(123L)
                .startTime(Clock.now().toDate())
                .build();

        when(schedulerBucket.counter("FutureJob_123", 1, 0, 3600)).thenReturn(JsonLongDocument.create("FutureJob_123", 0L));

        //when
        boolean result = jobSynchronizeService.isAssignableToThisExecution(futureJob);

        //then
        assertThat(result).isTrue();
        verify(schedulerBucket).counter("FutureJob_123", 1, 0, 3600);
        verifyNoMoreInteractions(schedulerBucket);
    }

    @Test
    @FrozenClock(value = "18/10/2018 14:30:40.555", format = "dd/MM/yyyy HH:mm:ss.SSS")
    public void it_should_return_false_when_future_job_is_assigned_to_other_instances() {
        //given
        FutureJob futureJob = aFutureJob()
                .id(123L)
                .startTime(Clock.now().toDate())
                .expireTime(Clock.now().plusHours(2).toDate())
                .build();

        when(schedulerBucket.counter("FutureJob_123", 1, 0, 7200)).thenReturn(JsonLongDocument.create("FutureJob_123", 1L));

        //when
        boolean result = jobSynchronizeService.isAssignableToThisExecution(futureJob);

        //then
        assertThat(result).isFalse();
        verify(schedulerBucket).counter("FutureJob_123", 1, 0, 7200);
        verifyNoMoreInteractions(schedulerBucket);
    }

    @Test
    @FrozenClock(value = "18/10/2018 14:30:40.555", format = "dd/MM/yyyy HH:mm:ss.SSS")
    public void it_should_return_false_when_future_job_is_assigned_to_other_instances_with_default_expire_date() {
        //given
        FutureJob futureJob = aFutureJob()
                .id(123L)
                .startTime(Clock.now().plusHours(1).toDate())
                .build();

        when(schedulerBucket.counter("FutureJob_123", 1, 0, 7200)).thenReturn(JsonLongDocument.create("FutureJob_123", 1L));

        //when
        boolean result = jobSynchronizeService.isAssignableToThisExecution(futureJob);

        //then
        assertThat(result).isFalse();
        verify(schedulerBucket).counter("FutureJob_123", 1, 0, 7200);
        verifyNoMoreInteractions(schedulerBucket);
    }
}