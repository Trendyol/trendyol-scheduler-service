package com.trendyol.scheduler.service;

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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static com.trendyol.scheduler.builder.FutureJobBuilder.aFutureJob;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author dilaverd
 * @since 10.12.2019
 */
@RunWith(MockitoJUnitRunner.class)
public class JobSynchronizeRedisServiceTest {

    @Rule
    public FrozenClock.Rule frozenClockRule = new FrozenClock.Rule();

    @InjectMocks
    private JobSynchronizeRedisService jobSynchronizeService;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private ValueOperations valueOperations;

    @Test
    public void it_should_return_true_when_scheduled_job_is_not_assigned_to_other_instances() {
        //Given
        Clock.freeze(DateTime.parse("2018-04-08T00:00:00"));
        ScheduledJob scheduledJob = RestScheduledJobBuilder.aRestScheduledJob().id(1).cronExpression("0 0 0/1 * * ?").build();

        final String jobKey = "ScheduledJob_1";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(jobKey)).thenReturn(null);

        //When
        boolean result = jobSynchronizeService.isAssignableToThisExecution(scheduledJob);

        //Then
        assertThat(result).isTrue();

        verify(redisTemplate.opsForValue()).get(jobKey);
        verify(redisTemplate.opsForValue()).set(jobKey, 1);
        verify(redisTemplate).expire(jobKey, 2880, TimeUnit.SECONDS);

        Clock.unfreeze();
    }

    @Test
    public void it_should_return_false_when_scheduled_job_is_assigned_to_other_instance() {
        //Given
        Clock.freeze(DateTime.parse("2018-04-08T00:00:00"));
        ScheduledJob scheduledJob = RestScheduledJobBuilder.aRestScheduledJob().id(1).cronExpression("0 0 0/1 * * ?").build();

        final String jobKey = "ScheduledJob_1";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(jobKey)).thenReturn(1L);

        //When
        boolean result = jobSynchronizeService.isAssignableToThisExecution(scheduledJob);

        //Then
        assertThat(result).isFalse();

        verify(redisTemplate.opsForValue()).get(jobKey);

        Clock.unfreeze();
        verifyNoMoreInteractions(redisTemplate.opsForValue());
    }

    @Test
    public void it_should_return_true_when_scheduled_job_cron_is_consisted_from_6_digits_for_not_already_assigned_to_other_instance() {
        //Given
        Clock.freeze(DateTime.parse("2018-04-08T00:00:00"));
        ScheduledJob scheduledJob = RestScheduledJobBuilder.aRestScheduledJob().id(1).cronExpression("0 0 * * * *").build();

        final String jobKey = "ScheduledJob_1";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(jobKey)).thenReturn(0L);

        //When
        boolean result = jobSynchronizeService.isAssignableToThisExecution(scheduledJob);

        //Then
        assertThat(result).isTrue();

        verify(redisTemplate.opsForValue()).get(jobKey);
        verify(redisTemplate).expire(jobKey, 2880, TimeUnit.SECONDS);

        Clock.unfreeze();
    }

    @Test
    public void it_should_return_true_with_ONE_MINUTE_TTL_counter_when_scheduled_job_next_execution_time_is_after_0_seconds() {
        //Given
        Clock.freeze(DateTime.parse("2018-04-08T00:00:09"));
        ScheduledJob scheduledJob = RestScheduledJobBuilder.aRestScheduledJob().id(1).cronExpression("*/5 * * * * *").build();

        final String jobKey = "ScheduledJob_1";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(jobKey)).thenReturn(0L);

        //When
        boolean result = jobSynchronizeService.isAssignableToThisExecution(scheduledJob);

        //Then
        assertThat(result).isTrue();

        verify(redisTemplate.opsForValue()).get(jobKey);
        verify(redisTemplate).expire(jobKey, 60, TimeUnit.SECONDS);

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

        final String jobKey = "FutureJob_123";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(jobKey)).thenReturn(0L);


        //when
        boolean result = jobSynchronizeService.isAssignableToThisExecution(futureJob);

        //then
        assertThat(result).isTrue();
        verify(redisTemplate.opsForValue()).get(jobKey);
        verify(redisTemplate).expire(jobKey, 7200, TimeUnit.SECONDS);
    }

    @Test
    @FrozenClock(value = "18/10/2018 14:30:40.555", format = "dd/MM/yyyy HH:mm:ss.SSS")
    public void it_should_return_true_when_future_job_is_not_assigned_to_other_instances_with_default_expire_date() {
        //given
        FutureJob futureJob = aFutureJob()
                .id(123L)
                .startTime(Clock.now().toDate())
                .build();

        final String jobKey = "FutureJob_123";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(jobKey)).thenReturn(0L);

        //when
        boolean result = jobSynchronizeService.isAssignableToThisExecution(futureJob);

        //then
        assertThat(result).isTrue();
        verify(redisTemplate.opsForValue()).get(jobKey);
        verify(redisTemplate).expire(jobKey, 3600, TimeUnit.SECONDS);
        verifyNoMoreInteractions(redisTemplate.opsForValue());
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

        final String jobKey = "FutureJob_123";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(jobKey)).thenReturn(1L);

        //when
        boolean result = jobSynchronizeService.isAssignableToThisExecution(futureJob);

        //then
        assertThat(result).isFalse();
        verify(redisTemplate.opsForValue()).get(jobKey);
        verify(redisTemplate).expire(jobKey, 7200, TimeUnit.SECONDS);
        verifyNoMoreInteractions(redisTemplate.opsForValue());
    }

    @Test
    @FrozenClock(value = "18/10/2018 14:30:40.555", format = "dd/MM/yyyy HH:mm:ss.SSS")
    public void it_should_return_false_when_future_job_is_assigned_to_other_instances_with_default_expire_date() {
        //given
        FutureJob futureJob = aFutureJob()
                .id(123L)
                .startTime(Clock.now().plusHours(1).toDate())
                .build();

        final String jobKey = "FutureJob_123";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(jobKey)).thenReturn(1L);

        //when
        boolean result = jobSynchronizeService.isAssignableToThisExecution(futureJob);

        //then
        assertThat(result).isFalse();
        verify(redisTemplate.opsForValue()).get(jobKey);
        verify(redisTemplate).expire(jobKey, 7200, TimeUnit.SECONDS);
        verifyNoMoreInteractions(redisTemplate.opsForValue());
    }
}