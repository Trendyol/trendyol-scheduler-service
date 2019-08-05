package com.trendyol.scheduler.service;

import com.trendyol.scheduler.FrozenClock;
import com.trendyol.scheduler.builder.domain.ScheduledJobExecutionHistoryBuilder;
import com.trendyol.scheduler.domain.ScheduledJobExecutionHistory;
import com.trendyol.scheduler.domain.enums.JobExecutionStatus;
import com.trendyol.scheduler.model.dto.ScheduledJobResultDto;
import com.trendyol.scheduler.utils.Clock;
import com.trendyol.scheduler.utils.DateUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledJobResultListenerTest {

    @Rule
    public FrozenClock.Rule frozenClock = new FrozenClock.Rule();

    @InjectMocks
    private ScheduledJobResultListener scheduledJobResultListener;

    @Mock
    private ScheduledJobExecutionHistoryService scheduledJobExecutionHistoryService;

    @Test
    @FrozenClock(value = "11/12/2013 14:15", format = "dd/MM/yyyy HH:mm")
    public void it_should_set_job_as_successfully_if_job_result_is_success() {
        //given
        ScheduledJobResultDto scheduledJobResultDto = ScheduledJobResultDto.success("taskId");

        ScheduledJobExecutionHistory scheduledJobExecutionHistory = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .id("taskId")
                .jobExecutionStatus(JobExecutionStatus.In_Progress)
                .startDate(DateUtils.toDateTime("11/12/2013 10:30").toDate())
                .build();
        when(scheduledJobExecutionHistoryService.getExecutionHistory("taskId", JobExecutionStatus.In_Progress)).thenReturn(Optional.of(scheduledJobExecutionHistory));

        //when
        scheduledJobResultListener.listenScheduledJobResult(scheduledJobResultDto);

        //then
        ArgumentCaptor<ScheduledJobExecutionHistory> historyCaptor = ArgumentCaptor.forClass(ScheduledJobExecutionHistory.class);
        verify(scheduledJobExecutionHistoryService).save(historyCaptor.capture());
        ScheduledJobExecutionHistory capturedHistory = historyCaptor.getValue();
        assertThat(capturedHistory.getId()).isEqualTo("taskId");
        assertThat(capturedHistory.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.Success);
        assertThat(capturedHistory.getStartDate()).isEqualTo(DateUtils.toDateTime("11/12/2013 10:30").toDate());
        assertThat(capturedHistory.getEndDate()).isEqualTo(Clock.now().toDate());
        assertThat(capturedHistory.getErrorDetail()).isNull();
    }

    @Test
    @FrozenClock(value = "11/12/2013 14:15", format = "dd/MM/yyyy HH:mm")
    public void it_should_set_job_as_failed_if_job_result_is_failed() {
        //given
        RuntimeException thrown = new RuntimeException("error");
        ScheduledJobResultDto scheduledJobResultDto = ScheduledJobResultDto.failed("taskId", 500, thrown);

        ScheduledJobExecutionHistory scheduledJobExecutionHistory = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .id("taskId")
                .jobExecutionStatus(JobExecutionStatus.In_Progress)
                .startDate(DateUtils.toDateTime("11/12/2013 10:30").toDate())
                .build();
        when(scheduledJobExecutionHistoryService.getExecutionHistory("taskId", JobExecutionStatus.In_Progress)).thenReturn(Optional.of(scheduledJobExecutionHistory));

        //when
        scheduledJobResultListener.listenScheduledJobResult(scheduledJobResultDto);

        //then
        ArgumentCaptor<ScheduledJobExecutionHistory> historyCaptor = ArgumentCaptor.forClass(ScheduledJobExecutionHistory.class);
        verify(scheduledJobExecutionHistoryService).save(historyCaptor.capture());
        ScheduledJobExecutionHistory capturedHistory = historyCaptor.getValue();
        assertThat(capturedHistory.getId()).isEqualTo("taskId");
        assertThat(capturedHistory.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.Failed);
        assertThat(capturedHistory.getStartDate()).isEqualTo(DateUtils.toDateTime("11/12/2013 10:30").toDate());
        assertThat(capturedHistory.getEndDate()).isEqualTo(Clock.now().toDate());
        assertThat(capturedHistory.getErrorDetail()).startsWith("HttpStatus: 500 - Detail: [java.lang.RuntimeException: error");
    }

    @Test
    @FrozenClock(value = "11/12/2013 14:15", format = "dd/MM/yyyy HH:mm")
    public void it_should_do_nothing_when_inProgress_job_history_not_found() {
        //given
        ScheduledJobResultDto scheduledJobResultDto = ScheduledJobResultDto.success("taskId");

        when(scheduledJobExecutionHistoryService.getExecutionHistory("taskId", JobExecutionStatus.In_Progress)).thenReturn(Optional.empty());

        //when
        scheduledJobResultListener.listenScheduledJobResult(scheduledJobResultDto);

        //then
        verify(scheduledJobExecutionHistoryService, never()).save(any());
    }

    @Test
    public void it_should_rethrow_exception_when_occurred() {
        //given
        ScheduledJobResultDto scheduledJobResultDto = ScheduledJobResultDto.success("taskId");

        when(scheduledJobExecutionHistoryService.getExecutionHistory("taskId", JobExecutionStatus.In_Progress))
                .thenThrow(new RuntimeException("error"));

        //when
        RuntimeException thrown = (RuntimeException) catchThrowable(() -> scheduledJobResultListener.listenScheduledJobResult(scheduledJobResultDto));

        //then
        assertThat(thrown.getMessage()).isEqualTo("error");
        verify(scheduledJobExecutionHistoryService, never()).save(any());
    }
}