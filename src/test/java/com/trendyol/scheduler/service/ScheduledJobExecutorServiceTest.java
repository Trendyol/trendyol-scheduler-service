package com.trendyol.scheduler.service;

import com.trendyol.scheduler.builder.domain.BeanScheduledJobBuilder;
import com.trendyol.scheduler.domain.BeanScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJobExecutionHistory;
import com.trendyol.scheduler.domain.enums.JobExecutionStatus;
import com.trendyol.scheduler.utils.Clock;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledJobExecutorServiceTest {

    @InjectMocks
    private ScheduledJobExecutorService scheduledJobExecutorService;

    @Mock
    private ScheduledJobExecutionHistoryService scheduledJobExecutionHistoryService;

    @Mock
    private RestTemplateScheduledJobExecutorService restTemplateScheduledJobExecutorService;

    @Mock
    private JobSynchronizeService jobSynchronizeService;

    @Test
    public void it_should_execute_rest_template_end_point_and_save_history_record_with_in_progress_status_when_scheduler_hits() {
        //Given
        Clock.freeze();
        BeanScheduledJob scheduledJob = BeanScheduledJobBuilder.aBeanScheduledJob().name("scheduler1").cronExpression("* * * * * *").methodName("method").beanName("bean").application("application").build();

        ArgumentCaptor<ScheduledJobExecutionHistory> scheduledJobExecutionHistoryArgumentCaptor = ArgumentCaptor.forClass(ScheduledJobExecutionHistory.class);

        when(jobSynchronizeService.isAssignableToThisExecution(scheduledJob)).thenReturn(true);

        //When
        scheduledJobExecutorService.execute(scheduledJob);

        //Then
        verify(scheduledJobExecutionHistoryService).save(scheduledJobExecutionHistoryArgumentCaptor.capture());

        ScheduledJobExecutionHistory capturedScheduledJobExecutionHistory = scheduledJobExecutionHistoryArgumentCaptor.getValue();
        assertThat(capturedScheduledJobExecutionHistory.getScheduledJob()).isEqualTo(scheduledJob);
        assertThat(capturedScheduledJobExecutionHistory.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.In_Progress);
        assertThat(capturedScheduledJobExecutionHistory.getStartDate()).isEqualTo(Clock.now().toDate());
        assertThat(capturedScheduledJobExecutionHistory.getEndDate()).isNull();

        Clock.unfreeze();
    }

    @Test
    public void it_should_execute_rest_template_end_point_and_save_history_record_with_fail_status_when_scheduler_hits_and_exception_occurred() {
        //Given
        Clock.freeze();
        BeanScheduledJob scheduledJob = BeanScheduledJobBuilder.aBeanScheduledJob().name("scheduler1").cronExpression("* * * * * *").methodName("method").beanName("bean").application("application").build();

        ArgumentCaptor<ScheduledJobExecutionHistory> scheduledJobExecutionHistoryArgumentCaptor = ArgumentCaptor.forClass(ScheduledJobExecutionHistory.class);

        RuntimeException thrownException = new RuntimeException("Runtime Exception Occurred");
        doThrow(thrownException).when(restTemplateScheduledJobExecutorService).executeWithTaskId(any(String.class), eq(scheduledJob));
        when(jobSynchronizeService.isAssignableToThisExecution(scheduledJob)).thenReturn(true);

        //When
        scheduledJobExecutorService.execute(scheduledJob);

        //Then
        verify(scheduledJobExecutionHistoryService, times(2)).save(scheduledJobExecutionHistoryArgumentCaptor.capture());

        ScheduledJobExecutionHistory failedScheduledJobExecutionHistory = scheduledJobExecutionHistoryArgumentCaptor.getAllValues().get(1);
        assertThat(failedScheduledJobExecutionHistory.getScheduledJob()).isEqualTo(scheduledJob);
        assertThat(failedScheduledJobExecutionHistory.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.Failed);
        assertThat(failedScheduledJobExecutionHistory.getErrorDetail()).isEqualTo(Arrays.deepToString(ExceptionUtils.getRootCauseStackTrace(thrownException)));
        assertThat(failedScheduledJobExecutionHistory.getStartDate()).isEqualTo(Clock.now().toDate());
        assertThat(failedScheduledJobExecutionHistory.getEndDate()).isEqualTo(Clock.now().toDate());

        Clock.unfreeze();
    }

    @Test
    public void it_should_execute_rest_template_end_point_and_save_failed_history_record_with_trimmed_exception_detail() {
        //Given
        Clock.freeze();
        BeanScheduledJob scheduledJob = BeanScheduledJobBuilder.aBeanScheduledJob().name("scheduler1").cronExpression("* * * * * *").methodName("method").beanName("bean").application("application").build();

        ArgumentCaptor<ScheduledJobExecutionHistory> scheduledJobExecutionHistoryArgumentCaptor = ArgumentCaptor.forClass(ScheduledJobExecutionHistory.class);

        String veryLongErrorMessage = RandomStringUtils.randomAlphabetic(10000);
        RuntimeException thrownException = new RuntimeException(veryLongErrorMessage);
        doThrow(thrownException).when(restTemplateScheduledJobExecutorService).executeWithTaskId(any(String.class), eq(scheduledJob));
        when(jobSynchronizeService.isAssignableToThisExecution(scheduledJob)).thenReturn(true);

        //When
        scheduledJobExecutorService.execute(scheduledJob);

        //Then
        verify(scheduledJobExecutionHistoryService, times(2)).save(scheduledJobExecutionHistoryArgumentCaptor.capture());

        List<ScheduledJobExecutionHistory> capturedScheduledJobExecutionHistory = scheduledJobExecutionHistoryArgumentCaptor.getAllValues();
        ScheduledJobExecutionHistory scheduledJobExecutionHistoryRecord = capturedScheduledJobExecutionHistory.get(0);
        assertThat(scheduledJobExecutionHistoryRecord.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.Failed);
        assertThat(scheduledJobExecutionHistoryRecord.getErrorDetail()).hasSize(4096);

        Clock.unfreeze();
    }

    @Test
    public void it_should_not_execute_when_job_is_assigned_to_other_instance() {
        Clock.freeze();
        BeanScheduledJob scheduledJob = BeanScheduledJobBuilder.aBeanScheduledJob().name("scheduler1").cronExpression("* * * * * *").methodName("method").beanName("bean").application("application").build();

        when(jobSynchronizeService.isAssignableToThisExecution(scheduledJob)).thenReturn(false);

        //When
        scheduledJobExecutorService.execute(scheduledJob);

        //Then
        verifyZeroInteractions(scheduledJobExecutionHistoryService);
        verifyZeroInteractions(restTemplateScheduledJobExecutorService);

        Clock.unfreeze();
    }
}