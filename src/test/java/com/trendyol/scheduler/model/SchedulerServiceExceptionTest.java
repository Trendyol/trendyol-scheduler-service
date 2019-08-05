package com.trendyol.scheduler.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SchedulerServiceExceptionTest {

    @Test
    public void it_should_create_with_empty_constructor() {
        //Given

        //When
        SchedulerServiceException schedulerServiceException = new SchedulerServiceException();

        //Then
        assertThat(schedulerServiceException).isNotNull();
    }

    @Test
    public void it_should_create_with_constructor_with_message_and_cause() {
        //Given
        Throwable throwable = new Throwable();

        //When
        SchedulerServiceException schedulerServiceException = new SchedulerServiceException("message", throwable);

        //Then
        assertThat(schedulerServiceException.getMessage()).isEqualTo("message");
        assertThat(schedulerServiceException.getCause()).isEqualTo(throwable);
    }

    @Test
    public void it_should_create_with_constructor_with_message_enable_suppression_and_writable_stack_trace() {
        //Given
        Throwable throwable = new Throwable();

        //When
        SchedulerServiceException schedulerServiceException = new SchedulerServiceException("message", throwable, true, true);

        //Then
        assertThat(schedulerServiceException.getMessage()).isEqualTo("message");
        assertThat(schedulerServiceException.getCause()).isEqualTo(throwable);
    }
}