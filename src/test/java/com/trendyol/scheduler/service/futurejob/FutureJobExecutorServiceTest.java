package com.trendyol.scheduler.service.futurejob;

import com.trendyol.scheduler.builder.FutureJobBuilder;
import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.domain.enums.FutureJobStatus;
import com.trendyol.scheduler.service.JobSynchronizer;
import com.trendyol.scheduler.util.Executor;
import org.junit.Rule;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

import static com.trendyol.scheduler.builder.FutureJobBuilder.aFutureJob;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FutureJobExecutorServiceTest {

    @Rule
    public Executor.Rule executorRule = new Executor.Rule();

    @InjectMocks
    private FutureJobExecutorService futureJobExecutorService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private FutureJobService futureJobService;

    @Mock
    private JobSynchronizer jobSynchronizer;

    @Captor
    private ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor;

    @Captor
    private ArgumentCaptor<FutureJob> futureJobArgumentCaptor;

    @Test
    public void it_should_execute_scheduled_future_job_successfully() {
        //given
        final FutureJob futureJob = aFutureJob()
                .id(1L)
                .futureJobStatus(FutureJobStatus.WAITING)
                .taskId("task-id")
                .application("app")
                .method("POST")
                .path("/path")
                .payload("123")
                .url("url")
                .build();

        when(jobSynchronizer.isAssignableToThisExecution(futureJob)).thenReturn(true);
        when(futureJobService.findById(1L)).thenReturn(Optional.empty());
        when(restTemplate.exchange(eq("url/path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.ACCEPTED));

        //when
        futureJobExecutorService.execute(futureJob);

        //then
        verify(restTemplate).exchange(eq("url/path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class));

        verify(futureJobService).save(futureJobArgumentCaptor.capture());
        final FutureJob futureJobArgumentCaptorValue = futureJobArgumentCaptor.getValue();
        assertThat(futureJobArgumentCaptorValue.getFutureJobStatus()).isEqualTo(FutureJobStatus.SUCCESS);
        assertThat(futureJobArgumentCaptorValue).isEqualToComparingFieldByFieldRecursively(futureJob);
    }

    @Test
    public void it_should_execute_scheduled_future_job_failed() {
        //given
        final FutureJob futureJob = aFutureJob()
                .id(1L)
                .futureJobStatus(FutureJobStatus.WAITING)
                .taskId("task-id")
                .application("app")
                .method("POST")
                .path("/path")
                .payload("123")
                .url("url")
                .build();

        when(jobSynchronizer.isAssignableToThisExecution(futureJob)).thenReturn(true);
        when(futureJobService.findById(1L)).thenReturn(Optional.empty());
        when(restTemplate.exchange(eq("url/path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        //when
        futureJobExecutorService.execute(futureJob);

        //then
        verify(restTemplate).exchange(eq("url/path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class));

        verify(futureJobService).save(futureJobArgumentCaptor.capture());
        final FutureJob futureJobArgumentCaptorValue = futureJobArgumentCaptor.getValue();
        assertThat(futureJobArgumentCaptorValue.getFutureJobStatus()).isEqualTo(FutureJobStatus.FAILED);
        assertThat(futureJobArgumentCaptorValue).isEqualToComparingFieldByFieldRecursively(futureJob);
    }

    @Test
    public void it_should_execute_scheduled_future_job_failed_when_exception_occurred() {
        //given
        final FutureJob futureJob = aFutureJob()
                .id(1L)
                .futureJobStatus(FutureJobStatus.WAITING)
                .taskId("task-id")
                .application("app")
                .method("POST")
                .path("/path")
                .payload("123")
                .url("url")
                .build();

        when(jobSynchronizer.isAssignableToThisExecution(futureJob)).thenReturn(true);
        when(futureJobService.findById(1L)).thenReturn(Optional.empty());
        when(restTemplate.exchange(eq("url/path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new RuntimeException("error"));

        //when
        final Throwable throwable = catchThrowable(() -> futureJobExecutorService.execute(futureJob));

        //then
        verify(restTemplate).exchange(eq("url/path"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Void.class));

        assertThat(throwable).isNull();

        verify(futureJobService).save(futureJobArgumentCaptor.capture());
        final FutureJob futureJobArgumentCaptorValue = futureJobArgumentCaptor.getValue();
        assertThat(futureJobArgumentCaptorValue.getFutureJobStatus()).isEqualTo(FutureJobStatus.FAILED);
        assertThat(futureJobArgumentCaptorValue).isEqualToComparingFieldByFieldRecursively(futureJob);
    }

    @Test
    @Executor(executorId = "executor-id", executorApp = "executor-app", executorUser = "executor-user")
    public void it_should_execute_scheduled_future_job_with_correlation_related_headers() {
        //given
        ReflectionTestUtils.setField(futureJobExecutorService, "applicationName", "app-name");

        final FutureJob futureJob = aFutureJob()
                .id(1L)
                .futureJobStatus(FutureJobStatus.WAITING)
                .taskId("task-id")
                .application("app")
                .method("POST")
                .path("/path")
                .payload("123")
                .url("url")
                .build();

        when(jobSynchronizer.isAssignableToThisExecution(futureJob)).thenReturn(true);
        when(futureJobService.findById(1L)).thenReturn(Optional.empty());

        //when
        futureJobExecutorService.execute(futureJob);

        //then
        verify(restTemplate).exchange(eq("url/path"), eq(HttpMethod.POST), httpEntityArgumentCaptor.capture(), eq(Void.class));
        final HttpEntity capturedHttpEntity = httpEntityArgumentCaptor.getValue();
        final HttpHeaders capturedHeaders = capturedHttpEntity.getHeaders();
        assertThat(capturedHeaders).containsEntry("x-correlationId", Collections.singletonList("task-id"));
        assertThat(capturedHeaders).containsEntry("x-agentname", Collections.singletonList("app-name"));
        assertThat(capturedHttpEntity.getBody().toString()).isEqualTo("123");
    }

    @Test
    public void it_should_not_execute_not_waiting_future_job_before_running() {
        //given
        FutureJob cancelledFutureJob = FutureJobBuilder.aFutureJob().id(10L).futureJobStatus(FutureJobStatus.CANCELLED).build();
        when(jobSynchronizer.isAssignableToThisExecution(cancelledFutureJob)).thenReturn(true);
        when(futureJobService.findById(10L)).thenReturn(Optional.ofNullable(cancelledFutureJob));

        //when
        futureJobExecutorService.execute(cancelledFutureJob);

        //then
        verify(futureJobService).findById(10L);
        verifyNoMoreInteractions(futureJobService);
    }

    @Test
    public void it_should_not_execute_future_job_when_job_assigned_to_another_instance() {
        //given
        final FutureJob futureJob = aFutureJob()
                .id(1L)
                .futureJobStatus(FutureJobStatus.WAITING)
                .taskId("task-id")
                .application("app")
                .method("POST")
                .path("/path")
                .payload("123")
                .url("url")
                .build();
        when(jobSynchronizer.isAssignableToThisExecution(futureJob)).thenReturn(false);

        //when
        futureJobExecutorService.execute(futureJob);

        //then
        verify(jobSynchronizer).isAssignableToThisExecution(futureJob);
        verifyZeroInteractions(futureJobService, restTemplate);
    }
}