package com.trendyol.scheduler.jobs;

import com.trendyol.scheduler.builder.domain.ScheduledJobExecutionHistoryBuilder;
import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJobExecutionHistory;
import com.trendyol.scheduler.domain.enums.JobExecutionStatus;
import com.trendyol.scheduler.service.JobSynchronizer;
import com.trendyol.scheduler.service.ScheduledJobExecutionHistoryService;
import com.trendyol.scheduler.utils.Clock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleJobStateCheckJobTest {

    private ScheduleJobStateCheckJob scheduleJobStateCheckJob;

    @Mock
    private JobSynchronizer jobSynchronizer;

    @Mock
    private ScheduledJobExecutionHistoryService scheduledJobExecutionHistoryService;

    @Before
    public void init() {
        this.scheduleJobStateCheckJob = new ScheduleJobStateCheckJob(
                new InternalJobExecutorTemplate(jobSynchronizer),
                scheduledJobExecutionHistoryService
        );
    }

    @Test
    public void it_should_make_stale_jobs_failed() {
        //given
        Clock.freeze();

        when(jobSynchronizer.isAssignableToThisExecution(any(ScheduledJob.class))).thenReturn(true);

        ScheduledJobExecutionHistory executionHistory1 = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .jobExecutionStatus(JobExecutionStatus.In_Progress)
                .build();
        ScheduledJobExecutionHistory executionHistory2 = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .jobExecutionStatus(JobExecutionStatus.In_Progress)
                .build();
        when(scheduledJobExecutionHistoryService.getStaleInProgressScheduledJobs()).thenReturn(Arrays.asList(executionHistory1, executionHistory2));

        //when
        scheduleJobStateCheckJob.checkStuckInProgressJobs();

        //then
        ArgumentCaptor<ScheduledJobExecutionHistory> scheduledJobExecutionHistoryCaptor = ArgumentCaptor.forClass(ScheduledJobExecutionHistory.class);
        verify(scheduledJobExecutionHistoryService, times(2)).save(scheduledJobExecutionHistoryCaptor.capture());
        List<ScheduledJobExecutionHistory> capturedHistories = scheduledJobExecutionHistoryCaptor.getAllValues();
        ScheduledJobExecutionHistory firstHistory = capturedHistories.get(0);
        assertThat(firstHistory.getErrorDetail()).isEqualTo("Failed due for being stale by ScheduleJobStateCheckJob");
        assertThat(firstHistory.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.Failed);
        assertThat(firstHistory.getEndDate()).isEqualTo(Clock.now().toDate());

        ScheduledJobExecutionHistory secondHistory = capturedHistories.get(1);
        assertThat(secondHistory.getErrorDetail()).isEqualTo("Failed due for being stale by ScheduleJobStateCheckJob");
        assertThat(secondHistory.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.Failed);
        assertThat(secondHistory.getEndDate()).isEqualTo(Clock.now().toDate());

        Clock.unfreeze();
    }
}