package com.trendyol.scheduler.service.futurejob;

import com.trendyol.scheduler.builder.FutureJobBuilder;
import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.domain.enums.FutureJobStatus;
import com.trendyol.scheduler.model.request.FutureJobRequest;
import com.trendyol.scheduler.util.Executor;
import com.trendyol.scheduler.utils.DateUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FutureJobCreatorServiceTest {

    @Rule
    public Executor.Rule executorRule = new Executor.Rule();

    @InjectMocks
    private FutureJobCreatorService futureJobCreatorService;

    @Mock
    private FutureJobService futureJobService;

    @Captor
    private ArgumentCaptor<FutureJob> futureJobArgumentCaptor;

    @Test
    @Executor(executorId = "executor-id", executorApp = "executor-app", executorUser = "executor-user")
    public void it_should_create_single_scheduling_future_job() {
        //given
        Date startDate = DateUtils.toDateTime("11/12/2013 15:55").toDate();
        Date expireDate = DateUtils.toDateTime("12/12/2099 15:55").toDate();
        FutureJobRequest futureJobRequest = FutureJobRequest.aSingleFutureJobRequest()
                .name("name")
                .application("app")
                .method("POST")
                .path("/path")
                .payload("payload")
                .startDate(startDate)
                .expireDate(expireDate);

        when(futureJobService.getWaitingFutureJobsByHashKey(futureJobRequest.getRequestUniqueKey())).thenReturn(Collections.emptyList());

        FutureJob savedFutureJob = FutureJobBuilder.aFutureJob().id(10L).build();
        when(futureJobService.save(any(FutureJob.class))).thenReturn(savedFutureJob);

        //when
        FutureJob actualFutureJob = futureJobCreatorService.createFutureJob(futureJobRequest);

        //then
        assertThat(actualFutureJob).isEqualTo(savedFutureJob);

        verify(futureJobService).save(futureJobArgumentCaptor.capture());
        FutureJob futureJob = futureJobArgumentCaptor.getValue();
        assertThat(futureJob.getFutureJobStatus()).isEqualTo(FutureJobStatus.WAITING);
        assertThat(futureJob.getTaskId()).isEqualTo("executor-id");
        assertThat(futureJob.getHashKey()).isEqualTo(futureJobRequest.getRequestUniqueKey());
        assertThat(futureJob.getName()).isEqualTo("name");
        assertThat(futureJob.getApplication()).isEqualTo("app");
        assertThat(futureJob.getMethod()).isEqualTo("POST");
        assertThat(futureJob.getPath()).isEqualTo("/path");
        assertThat(futureJob.getPayload()).isEqualTo("payload");
        assertThat(futureJob.getStartTime()).isEqualTo(startDate);
        assertThat(futureJob.getExpireTime()).isEqualTo(expireDate);
    }

    @Test
    @Executor(executorId = "executor-id", executorApp = "executor-app", executorUser = "executor-user")
    public void it_should_create_single_scheduling_future_job_after_cancelling_existing_future_job_with_same_hash_key() {
        //given
        Date startDate = DateUtils.toDateTime("11/12/2013 15:55").toDate();
        Date expireDate = DateUtils.toDateTime("12/12/2099 15:55").toDate();
        FutureJobRequest futureJobRequest = FutureJobRequest.aSingleFutureJobRequest()
                .name("name")
                .application("app")
                .method("POST")
                .path("/path")
                .payload("payload")
                .startDate(startDate)
                .expireDate(expireDate);

        FutureJob existingFutureJob = FutureJobBuilder.aFutureJob().id(10L).build();
        when(futureJobService.getWaitingFutureJobsByHashKey(futureJobRequest.getRequestUniqueKey()))
                .thenReturn(Collections.singletonList(existingFutureJob));

        FutureJob savedFutureJob = FutureJobBuilder.aFutureJob().id(11L).build();
        when(futureJobService.save(any(FutureJob.class))).thenReturn(savedFutureJob);

        //when
        FutureJob actualFutureJob = futureJobCreatorService.createFutureJob(futureJobRequest);

        //then
        assertThat(actualFutureJob).isEqualTo(savedFutureJob);

        verify(futureJobService, times(2)).save(futureJobArgumentCaptor.capture());
        List<FutureJob> allCapturedFutureJobs = futureJobArgumentCaptor.getAllValues();

        FutureJob cancelledFutureJob = allCapturedFutureJobs.get(0);
        assertThat(cancelledFutureJob.getId()).isEqualTo(10L);
        assertThat(cancelledFutureJob.getFutureJobStatus()).isEqualTo(FutureJobStatus.CANCELLED);

        FutureJob newFutureJob = allCapturedFutureJobs.get(1);
        assertThat(newFutureJob.getId()).isNull();
        assertThat(newFutureJob.getFutureJobStatus()).isEqualTo(FutureJobStatus.WAITING);
    }

    @Test
    @Executor(executorId = "executor-id", executorApp = "executor-app", executorUser = "executor-user")
    public void it_should_create_multiple_scheduling_future_job_without_cancelling_existing_future_jobs_with_same_hash_key() {
        //given
        Date startDate = DateUtils.toDateTime("11/12/2013 15:55").toDate();
        Date expireDate = DateUtils.toDateTime("12/12/2099 15:55").toDate();
        FutureJobRequest futureJobRequest = FutureJobRequest.aMultipleFutureJobRequest()
                .name("name")
                .application("app")
                .method("POST")
                .path("/path")
                .payload("payload")
                .startDate(startDate)
                .expireDate(expireDate);

        FutureJob savedFutureJob = FutureJobBuilder.aFutureJob().id(11L).build();
        when(futureJobService.save(any(FutureJob.class))).thenReturn(savedFutureJob);

        //when
        FutureJob actualFutureJob = futureJobCreatorService.createFutureJob(futureJobRequest);

        //then
        assertThat(actualFutureJob).isEqualTo(savedFutureJob);

        verify(futureJobService).save(futureJobArgumentCaptor.capture());
        FutureJob capturedFutureJob = futureJobArgumentCaptor.getValue();
        assertThat(capturedFutureJob.getId()).isNull();
        assertThat(capturedFutureJob.getFutureJobStatus()).isEqualTo(FutureJobStatus.WAITING);

        verifyNoMoreInteractions(futureJobService);
    }

}