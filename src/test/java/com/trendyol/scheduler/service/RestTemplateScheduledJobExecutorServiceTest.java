package com.trendyol.scheduler.service;

import com.trendyol.scheduler.builder.domain.BeanScheduledJobBuilder;
import com.trendyol.scheduler.domain.BeanScheduledJob;
import com.trendyol.scheduler.domain.RestScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.model.ExecutionContextModel;
import com.trendyol.scheduler.model.SchedulerServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;

import static com.trendyol.scheduler.builder.domain.RestScheduledJobBuilder.aRestScheduledJob;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestTemplateScheduledJobExecutorServiceTest {

    @InjectMocks
    private RestTemplateScheduledJobExecutorService restTemplateScheduledJobExecutorService;

    @Mock
    private AsyncRestTemplate restTemplate;

    @Mock
    private ListenableFuture<ResponseEntity<String>> listenableFuture;

    @Captor
    private ArgumentCaptor<HttpEntity<ExecutionContextModel>> httpEntityCaptor;

    @Before
    public void init() {
        ReflectionTestUtils.setField(restTemplateScheduledJobExecutorService, "applicationName", "SchedulerService");
    }

    @Test
    public void it_should_call_scheduler_end_point_of_given_application_when_job_type_is_bean_scheduled_job() {
        //Given
        BeanScheduledJob scheduledJob = BeanScheduledJobBuilder.aBeanScheduledJob().active(true).url("applicationendpoint").build();
        String taskId = "taskId";

        when(restTemplate.exchange(
                eq("applicationendpoint/scheduler/invoke"),
                eq(HttpMethod.POST), httpEntityCaptor.capture(),
                eq(String.class))
        ).thenReturn(listenableFuture);

        //When
        restTemplateScheduledJobExecutorService.executeWithTaskId(taskId, scheduledJob);
        //Then
        verify(restTemplate).exchange(
                eq("applicationendpoint/scheduler/invoke"),
                eq(HttpMethod.POST), httpEntityCaptor.capture(),
                eq(String.class)
        );

        HttpEntity<ExecutionContextModel> capturedHttpEntity = httpEntityCaptor.getValue();
        ExecutionContextModel capturedExecutionContextModel = capturedHttpEntity.getBody();
        assertThat(capturedExecutionContextModel.getBean()).isEqualTo(scheduledJob.getBeanName());
        assertThat(capturedExecutionContextModel.getMethod()).isEqualTo(scheduledJob.getMethodName());
        assertThat(capturedExecutionContextModel.getTaskId()).isEqualTo(taskId);

        HttpHeaders headers = capturedHttpEntity.getHeaders();
        assertThat(headers.get("x-correlationId")).containsOnly(taskId);
        assertThat(headers.get("x-agentname")).containsOnly("SchedulerService");
        assertThat(headers.get("Content-Type")).containsOnly("application/json");
    }

    @Test
    public void it_should_call_given_end_point_of_given_application_when_job_type_is_rest_scheduled_job() {
        //Given
        RestScheduledJob scheduledJob = aRestScheduledJob()
                .active(true)
                .method("POST")
                .path("someurl")
                .payload("foo")
                .url("applicationendpoint/")
                .build();
        String taskId = "taskId";

        when(restTemplate.exchange(
                eq("applicationendpoint/someurl"),
                eq(HttpMethod.POST), httpEntityCaptor.capture(),
                eq(String.class)
        )).thenReturn(listenableFuture);

        //When
        restTemplateScheduledJobExecutorService.executeWithTaskId(taskId, scheduledJob);

        //Then
        verify(restTemplate).exchange(
                eq("applicationendpoint/someurl"),
                eq(HttpMethod.POST), httpEntityCaptor.capture(),
                eq(String.class)
        );

        HttpEntity capturedHttpEntity = httpEntityCaptor.getValue();
        assertThat(capturedHttpEntity.getBody()).isEqualTo("foo");

        HttpHeaders headers = capturedHttpEntity.getHeaders();
        assertThat(headers.get("x-correlationId")).containsOnly(taskId);
        assertThat(headers.get("x-agentname")).containsOnly("SchedulerService");
        assertThat(headers.get("Content-Type")).containsOnly("application/json");
    }

    @Test
    public void it_should_throw_scheduler_service_exception_when_job_type_is_not_known() {
        //Given
        ScheduledJob scheduledJob = mock(ScheduledJob.class);
        String taskId = "taskId";

        //When
        Throwable throwable = catchThrowable(() -> restTemplateScheduledJobExecutorService.executeWithTaskId(taskId, scheduledJob));

        //Then
        assertThat(throwable).isInstanceOf(SchedulerServiceException.class);
    }

    @Test
    public void it_should_throw_scheduler_service_exception_when_ConnectionException_occurs_for_bean_based_jobs()
            throws ExecutionException, InterruptedException {
        //Given
        BeanScheduledJob scheduledJob = BeanScheduledJobBuilder.aBeanScheduledJob().active(true).url("applicationendpoint").build();
        String taskId = "taskId";

        when(restTemplate.exchange(
                eq("applicationendpoint/scheduler/invoke"),
                eq(HttpMethod.POST), any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(listenableFuture);
        when(listenableFuture.get()).thenThrow(new ExecutionException(new ConnectException("connection-error")));

        //When
        Throwable throwable = catchThrowable(() -> restTemplateScheduledJobExecutorService.executeWithTaskId(taskId, scheduledJob));

        //Then
        assertThat(throwable).isInstanceOf(SchedulerServiceException.class);
    }

    @Test
    public void it_should_throw_scheduler_service_exception_when_ConnectionException_occurs_for_rest_based_jobs()
            throws ExecutionException, InterruptedException {
        //Given
        RestScheduledJob scheduledJob = aRestScheduledJob()
                .active(true)
                .method("POST")
                .path("someurl")
                .payload("foo")
                .url("applicationendpoint/")
                .build();
        String taskId = "taskId";

        when(restTemplate.exchange(
                eq("applicationendpoint/someurl"),
                eq(HttpMethod.POST), any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(listenableFuture);
        when(listenableFuture.get()).thenThrow(new ExecutionException(new ConnectException("connection-error")));

        //When
        Throwable throwable = catchThrowable(() -> restTemplateScheduledJobExecutorService.executeWithTaskId(taskId, scheduledJob));

        //Then
        assertThat(throwable).isInstanceOf(SchedulerServiceException.class);
    }

    @Test
    public void it_should_do_nothing_when_runtime_exception_occurs_other_than_ConnectionException()
            throws ExecutionException, InterruptedException {
        //Given
        RestScheduledJob scheduledJob = aRestScheduledJob().active(true).method("POST").path("someurl").payload("foo").url("applicationendpoint/").build();
        String taskId = "taskId";

        when(restTemplate.exchange(
                eq("applicationendpoint/someurl"),
                eq(HttpMethod.POST), any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(listenableFuture);
        when(listenableFuture.get()).thenThrow(new ExecutionException(new SocketTimeoutException("read-timeout-error")));

        //When
        restTemplateScheduledJobExecutorService.executeWithTaskId(taskId, scheduledJob);
    }
}