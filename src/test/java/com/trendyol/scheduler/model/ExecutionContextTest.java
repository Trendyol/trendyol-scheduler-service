package com.trendyol.scheduler.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExecutionContextTest {

    @Test
    public void it_should_convert_to_string_properly() {
        //Given
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setMethod("method");
        executionContext.setBean("bean");
        executionContext.setTaskId("task-id");

        //When
        String executionContextStringValue = executionContext.toString();

        //Then
        assertThat(executionContextStringValue).isEqualTo("ExecutionContext[taskId=task-id,bean=bean,method=method]");
    }
}