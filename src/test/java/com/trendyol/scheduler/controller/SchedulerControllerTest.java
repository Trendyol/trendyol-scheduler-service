package com.trendyol.scheduler.controller;

import com.trendyol.scheduler.model.ExecutionContext;
import com.trendyol.scheduler.service.sample.SampleJobService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerControllerTest {

    @InjectMocks
    private SchedulerController schedulerController;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SampleJobService sampleJobService;

    @Test
    public void it_should_invoke_scheduler_invoke_endpoint() {
        //Given
        when(applicationContext.getBean("sampleJobService")).thenReturn(sampleJobService);

        ExecutionContext executionContext = new ExecutionContext();
        executionContext.setBean("sampleJobService");
        executionContext.setMethod("invoke");

        //When
        ResponseEntity response = schedulerController.invoke(executionContext);

        //Then
        verify(sampleJobService).invoke();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(StringUtils.EMPTY);
    }
}