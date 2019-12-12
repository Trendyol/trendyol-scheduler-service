package com.trendyol.scheduler.jobs;

import com.trendyol.scheduler.builder.domain.RestScheduledJobBuilder;
import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.service.JobSynchronizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InternalJobExecutorTemplateTest {

    @InjectMocks
    private InternalJobExecutorTemplate internalJobExecutorTemplate;

    @Mock
    private JobSynchronizer jobSynchronizer;

    @Mock
    private Runnable runnable;

    @Test
    public void it_should_run_scheduled_job() {
        //Given
        ScheduledJob scheduledJob = RestScheduledJobBuilder.aRestScheduledJob().build();
        when(jobSynchronizer.isAssignableToThisExecution(scheduledJob)).thenReturn(true);

        //When
        internalJobExecutorTemplate.run(scheduledJob, runnable);

        //Then
        verify(runnable).run();
    }

    @Test
    public void it_should_not_run_job_when_is_not_assignable() {
        //Given
        ScheduledJob scheduledJob = RestScheduledJobBuilder.aRestScheduledJob().build();
        when(jobSynchronizer.isAssignableToThisExecution(scheduledJob)).thenReturn(false);

        //When
        internalJobExecutorTemplate.run(scheduledJob, runnable);

        //Then
        verifyZeroInteractions(runnable);
    }
}