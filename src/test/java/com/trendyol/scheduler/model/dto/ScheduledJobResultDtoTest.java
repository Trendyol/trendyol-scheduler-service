package com.trendyol.scheduler.model.dto;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduledJobResultDtoTest {

    @Test
    public void it_should_convert_to_string_properly() {
        //Given
        ScheduledJobResultDto scheduledJobResultDto = new ScheduledJobResultDto();
        scheduledJobResultDto.setTaskId("task-id");
        scheduledJobResultDto.setSuccess(true);

        //When
        String scheduledJobResultStringValue = scheduledJobResultDto.toString();

        //Then
        assertThat(scheduledJobResultStringValue).isEqualTo("ScheduledJobResultDto[taskId=task-id,success=true]");
    }
}