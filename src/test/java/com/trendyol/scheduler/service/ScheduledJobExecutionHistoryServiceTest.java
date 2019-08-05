package com.trendyol.scheduler.service;

import com.trendyol.scheduler.builder.domain.RestScheduledJobBuilder;
import com.trendyol.scheduler.builder.domain.ScheduledJobExecutionHistoryBuilder;
import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJobExecutionHistory;
import com.trendyol.scheduler.domain.enums.JobExecutionStatus;
import com.trendyol.scheduler.repository.ScheduledJobExecutionHistoryRepository;
import com.trendyol.scheduler.utils.Clock;
import com.trendyol.scheduler.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledJobExecutionHistoryServiceTest {

    @InjectMocks
    private ScheduledJobExecutionHistoryService scheduledJobExecutionHistoryService;

    @Mock
    private ScheduledJobExecutionHistoryRepository scheduledJobExecutionHistoryRepository;

    @Test
    public void it_should_save_job_execution_history() {
        //Given
        ScheduledJobExecutionHistory scheduledJobExecutionHistory = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory().build();
        //When
        scheduledJobExecutionHistoryService.save(scheduledJobExecutionHistory);
        //Then
        verify(scheduledJobExecutionHistoryRepository).save(scheduledJobExecutionHistory);
    }

    @Test
    public void it_should_get_scheduled_job_execution_history_of_job_by_id_and_status() {
        //Given
        ScheduledJobExecutionHistory scheduledJobExecutionHistory = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory().build();
        when(scheduledJobExecutionHistoryRepository.findByIdAndJobExecutionStatus("taskId", JobExecutionStatus.In_Progress)).thenReturn(Optional.of(scheduledJobExecutionHistory));

        //When
        Optional<ScheduledJobExecutionHistory> foundHistory = scheduledJobExecutionHistoryService.getExecutionHistory("taskId", JobExecutionStatus.In_Progress);

        //Then
        assertThat(foundHistory).isPresent();
        assertThat(foundHistory.get()).isEqualTo(scheduledJobExecutionHistory);
        verify(scheduledJobExecutionHistoryRepository).findByIdAndJobExecutionStatus("taskId", JobExecutionStatus.In_Progress);
    }

    @Test
    public void it_should_get_scheduled_job_execution_history_of_job_by_scheduled_job_and_status() {
        //Given
        ScheduledJob scheduledJob = RestScheduledJobBuilder.aRestScheduledJob().id(10).build();
        ScheduledJobExecutionHistory scheduledJobExecutionHistory = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .scheduledJob(scheduledJob)
                .build();
        when(scheduledJobExecutionHistoryRepository.findByScheduledJobAndJobExecutionStatus(scheduledJob, JobExecutionStatus.In_Progress)).thenReturn(Optional.of(scheduledJobExecutionHistory));

        //When
        Optional<ScheduledJobExecutionHistory> foundHistory = scheduledJobExecutionHistoryService.getExecutionHistory(scheduledJob, JobExecutionStatus.In_Progress);

        //Then
        assertThat(foundHistory).isPresent();
        assertThat(foundHistory.get()).isEqualTo(scheduledJobExecutionHistory);
        verify(scheduledJobExecutionHistoryRepository).findByScheduledJobAndJobExecutionStatus(scheduledJob, JobExecutionStatus.In_Progress);
    }

    @Test
    public void it_should_get_stale_in_progress_scheduled_job_execution_histories() {
        //Given
        Clock.freeze(DateUtils.toDateTime("11/12/2013 14:15"));

        ScheduledJobExecutionHistory scheduledJobExecutionHistory1 = new ScheduledJobExecutionHistory();
        ScheduledJobExecutionHistory scheduledJobExecutionHistory2 = new ScheduledJobExecutionHistory();
        when(scheduledJobExecutionHistoryRepository.findAllByJobExecutionStatusAndStartDateIsBefore(eq(JobExecutionStatus.In_Progress), any(Date.class)))
                .thenReturn(Arrays.asList(scheduledJobExecutionHistory1, scheduledJobExecutionHistory2));

        //When
        List<ScheduledJobExecutionHistory> foundHistories = scheduledJobExecutionHistoryService.getStaleInProgressScheduledJobs();

        //Then
        assertThat(foundHistories).containsOnlyOnce(scheduledJobExecutionHistory1, scheduledJobExecutionHistory2);
        ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);
        verify(scheduledJobExecutionHistoryRepository).findAllByJobExecutionStatusAndStartDateIsBefore(eq(JobExecutionStatus.In_Progress), dateCaptor.capture());
        assertThat(dateCaptor.getValue()).isEqualTo(DateUtils.toDateTime("10/12/2013 00:00").toDate());

        Clock.unfreeze();
    }
}