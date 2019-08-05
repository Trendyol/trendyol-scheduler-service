package com.trendyol.scheduler.service;

import com.trendyol.scheduler.builder.domain.BeanScheduledJobBuilder;
import com.trendyol.scheduler.domain.BeanScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.repository.ScheduledJobRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledJobServiceTest {

    @InjectMocks
    private ScheduledJobService scheduledJobService;

    @Mock
    private ScheduledJobRepository scheduledJobRepository;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private ScheduledJobExecutorService scheduledJobExecutorService;


    @Test
    public void it_should_load_all_active_scheduled_jobs_and_schedule() {
        //Given
        BeanScheduledJob scheduledJob1 = BeanScheduledJobBuilder.aBeanScheduledJob().name("scheduler1").cronExpression("* * * * * *").build();
        BeanScheduledJob scheduledJob2 = BeanScheduledJobBuilder.aBeanScheduledJob().name("scheduler2").cronExpression("* * * * * *").build();

        when(scheduledJobRepository.findAllByActive(true)).thenReturn(Arrays.asList(scheduledJob1, scheduledJob2));
        ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        //When
        scheduledJobService.initialize();

        //Then
        verify(taskScheduler, times(2)).schedule(runnableArgumentCaptor.capture(), any(CronTrigger.class));

        List<Runnable> capturedValues = runnableArgumentCaptor.getAllValues();

        assertThat(capturedValues).hasSize(2);
    }

    @Test
    public void it_should_save_and_schedule_job() {
        //Given
        ScheduledJob scheduledJob1 = BeanScheduledJobBuilder.aBeanScheduledJob().name("scheduler1").cronExpression("* * * * * *").build();

        //When
        scheduledJobService.save(scheduledJob1);

        //Then
        verify(taskScheduler).schedule(any(Runnable.class), any(CronTrigger.class));

    }
}