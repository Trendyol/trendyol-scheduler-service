package com.trendyol.scheduler.service.futurejob;

import com.trendyol.scheduler.builder.FutureJobBuilder;
import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.model.request.FutureJobRequest;
import com.trendyol.scheduler.util.Executor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FutureJobListenerServiceTest {

    @Rule
    public Executor.Rule executorRule = new Executor.Rule();

    @InjectMocks
    private FutureJobListenerService futureJobListenerService;

    @Mock
    private FutureJobCreatorService futureJobCreatorService;

    @Mock
    private FutureJobSchedulerService futureJobSchedulerService;

    @Test
    @Executor(executorId = "executor-id", executorApp = "executor-app", executorUser = "executor-user")
    public void it_should_save_and_schedule_future_job() {
        //given
        FutureJobRequest req = FutureJobRequest.aSingleFutureJobRequest();

        FutureJob futureJob = FutureJobBuilder.aFutureJob().id(10L).build();
        when(futureJobCreatorService.createFutureJob(req)).thenReturn(futureJob);

        //when
        futureJobListenerService.scheduleFutureJob(req);

        //then
        verify(futureJobCreatorService).createFutureJob(req);
        verify(futureJobSchedulerService).schedule(futureJob);
    }

    @Test
    public void it_should_throw_exception_as_is() {
        //given
        FutureJobRequest req = FutureJobRequest.aSingleFutureJobRequest();

        FutureJob futureJob = FutureJobBuilder.aFutureJob().id(10L).build();
        when(futureJobCreatorService.createFutureJob(req)).thenReturn(futureJob);
        doThrow(new RuntimeException("error")).when(futureJobSchedulerService).schedule(futureJob);

        //when
        RuntimeException thrown = (RuntimeException) catchThrowable(() -> futureJobListenerService.scheduleFutureJob(req));

        //then
        assertThat(thrown.getMessage()).isEqualTo("error");
        verify(futureJobCreatorService).createFutureJob(req);
        verify(futureJobSchedulerService).schedule(futureJob);
    }

    @Test
    public void it_should_throw_exception_when_exception_occured_in_database() {
        //given
        FutureJobRequest req = FutureJobRequest.aSingleFutureJobRequest();

        when(futureJobCreatorService.createFutureJob(req)).thenThrow(new RuntimeException("error"));

        //when
        RuntimeException thrown = (RuntimeException) catchThrowable(() -> futureJobListenerService.scheduleFutureJob(req));

        //then
        assertThat(thrown.getMessage()).isEqualTo("error");
        verify(futureJobCreatorService).createFutureJob(req);
    }
}