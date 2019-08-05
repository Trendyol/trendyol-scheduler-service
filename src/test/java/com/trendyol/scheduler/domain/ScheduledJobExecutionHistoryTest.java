package com.trendyol.scheduler.domain;

import com.trendyol.scheduler.FrozenClock;
import com.trendyol.scheduler.domain.enums.JobExecutionStatus;
import com.trendyol.scheduler.utils.Clock;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduledJobExecutionHistoryTest {

    @Rule
    public FrozenClock.Rule clockRule = new FrozenClock.Rule();

    @Test
    @FrozenClock("11/12/2013")
    public void it_should_fail_scheduler_execution_history() {
        //given
        ScheduledJobExecutionHistory history = new ScheduledJobExecutionHistory();
        history.setJobExecutionStatus(JobExecutionStatus.In_Progress);

        RuntimeException thrown = new RuntimeException("error");

        //when
        history.fail(thrown);

        //then
        assertThat(history.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.Failed);
        assertThat(history.getEndDate()).isEqualTo(Clock.now().toDate());
        assertThat(history.getErrorDetail()).contains("java.lang.RuntimeException: error");
    }

    @Test
    @FrozenClock("11/12/2013")
    public void it_should_fail_scheduler_execution_history_with_trimmed_error_message() {
        //given
        ScheduledJobExecutionHistory history = new ScheduledJobExecutionHistory();
        history.setJobExecutionStatus(JobExecutionStatus.In_Progress);

        String veryLongErrorMessage = RandomStringUtils.randomAlphabetic(10000);
        RuntimeException thrown = new RuntimeException(veryLongErrorMessage);

        //when
        history.fail(thrown);

        //then
        assertThat(history.getJobExecutionStatus()).isEqualTo(JobExecutionStatus.Failed);
        assertThat(history.getEndDate()).isEqualTo(Clock.now().toDate());
        assertThat(history.getErrorDetail()).hasSize(4096);
    }

    @Test
    public void it_should_return_false_when_compared_object_is_different_type() {
        //Given
        ScheduledJobExecutionHistory scheduledJobExecutionHistory = new ScheduledJobExecutionHistory();
        Object object = new Object();

        //When
        boolean equals = scheduledJobExecutionHistory.equals(object);

        //Then
        assertThat(equals).isFalse();
    }
}