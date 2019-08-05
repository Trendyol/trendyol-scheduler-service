package com.trendyol.scheduler.service.futurejob;

import com.trendyol.scheduler.builder.FutureJobBuilder;
import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.domain.enums.FutureJobStatus;
import com.trendyol.scheduler.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FutureJobSchedulerServiceTest {

    @InjectMocks
    private FutureJobSchedulerService futureJobSchedulerService;

    @Mock
    private FutureJobService futureJobService;

    @Mock
    private TaskScheduler futureJobTaskScheduler;

    @Mock
    private FutureJobExecutorService futureJobExecutorService;

    @Captor
    private ArgumentCaptor<FutureJob> futureJobArgumentCaptor;

    @Test
    public void it_should_schedule_future_job_on_startup() {
        //given
        FutureJob futureJob1 = FutureJobBuilder.aFutureJob()
                .startTime(DateUtils.toDateTimeWithSecond("20/12/2013 15:55:00").toDate())
                .expireTime(DateUtils.toDateTimeWithSecond("31/12/2099 15:55:00").toDate())
                .build();
        FutureJob futureJob2 = FutureJobBuilder.aFutureJob()
                .startTime(DateUtils.toDateTimeWithSecond("11/12/2013 15:55:00").toDate())
                .expireTime(DateUtils.toDateTimeWithSecond("12/12/2099 15:55:00").toDate())
                .build();
        when(futureJobService.getWaitingFutureJobs()).thenReturn(Arrays.asList(futureJob1, futureJob2));

        //when
        futureJobSchedulerService.initialize();

        //then
        verify(futureJobTaskScheduler).schedule(any(Runnable.class), eq(DateUtils.toDateTimeWithSecond("20/12/2013 15:55:02").toDate()));
        verify(futureJobTaskScheduler).schedule(any(Runnable.class), eq(DateUtils.toDateTimeWithSecond("11/12/2013 15:55:02").toDate()));
        verify(futureJobService).getWaitingFutureJobs();
        verifyNoMoreInteractions(futureJobTaskScheduler, futureJobService);
    }

    @Test
    public void it_should_schedule_future_job_without_expire_date_on_startup() {
        //given
        FutureJob futureJob1 = FutureJobBuilder.aFutureJob()
                .futureJobStatus(FutureJobStatus.WAITING)
                .startTime(DateUtils.toDateTimeWithSecond("20/12/2013 15:55:00").toDate())
                .build();
        FutureJob futureJob2 = FutureJobBuilder.aFutureJob()
                .futureJobStatus(FutureJobStatus.WAITING)
                .startTime(DateUtils.toDateTimeWithSecond("11/12/2013 15:55:00").toDate())
                .build();
        when(futureJobService.getWaitingFutureJobs()).thenReturn(Arrays.asList(futureJob1, futureJob2));

        //when
        futureJobSchedulerService.initialize();

        //then
        verify(futureJobTaskScheduler).schedule(any(Runnable.class), eq(DateUtils.toDateTimeWithSecond("20/12/2013 15:55:02").toDate()));
        verify(futureJobTaskScheduler).schedule(any(Runnable.class), eq(DateUtils.toDateTimeWithSecond("11/12/2013 15:55:02").toDate()));
    }

    @Test
    public void it_should_not_schedule_expired_future_jobs_and_change_status_to_EXPIRED_on_startup() {
        //given
        FutureJob futureJob = FutureJobBuilder.aFutureJob()
                .futureJobStatus(FutureJobStatus.WAITING)
                .startTime(DateUtils.toDateTimeWithSecond("20/12/2013 15:55:00").toDate())
                .expireTime(DateUtils.toDateTimeWithSecond("31/12/2099 15:55:00").toDate())
                .build();
        FutureJob expiredFutureJob = FutureJobBuilder.aFutureJob()
                .futureJobStatus(FutureJobStatus.WAITING)
                .startTime(DateUtils.toDateTimeWithSecond("11/12/2013 15:55:00").toDate())
                .expireTime(DateUtils.toDateTimeWithSecond("12/12/2013 15:55:00").toDate())
                .build();
        when(futureJobService.getWaitingFutureJobs()).thenReturn(Arrays.asList(futureJob, expiredFutureJob));

        //when
        futureJobSchedulerService.initialize();

        //then
        verify(futureJobService).getWaitingFutureJobs();
        verify(futureJobTaskScheduler).schedule(any(Runnable.class), eq(DateUtils.toDateTimeWithSecond("20/12/2013 15:55:02").toDate()));
        verify(futureJobService).save(futureJobArgumentCaptor.capture());
        final FutureJob expiredFeatureJob = futureJobArgumentCaptor.getValue();
        assertThat(expiredFeatureJob.getFutureJobStatus()).isEqualTo(FutureJobStatus.EXPIRED);
        verifyNoMoreInteractions(futureJobTaskScheduler, futureJobService);
    }

    @Test
    public void it_should_schedule_future_job() {
        //given
        FutureJob futureJob = FutureJobBuilder.aFutureJob()
                .startTime(DateUtils.toDateTimeWithSecond("20/12/2013 15:55:05").toDate())
                .expireTime(DateUtils.toDateTimeWithSecond("31/12/2099 15:55:05").toDate())
                .build();

        //when
        futureJobSchedulerService.schedule(futureJob);

        //then
        verify(futureJobTaskScheduler).schedule(any(Runnable.class), eq(DateUtils.toDateTimeWithSecond("20/12/2013 15:55:07").toDate()));
        verifyZeroInteractions(futureJobService);
    }

    @Test
    public void it_should_schedule_future_job_without_expire_date() {
        //given
        FutureJob futureJob = FutureJobBuilder.aFutureJob()
                .futureJobStatus(FutureJobStatus.WAITING)
                .startTime(DateUtils.toDateTimeWithSecond("20/12/2013 15:55:20").toDate())
                .build();

        //when
        futureJobSchedulerService.schedule(futureJob);

        //then
        verify(futureJobTaskScheduler).schedule(any(Runnable.class), eq(DateUtils.toDateTimeWithSecond("20/12/2013 15:55:22").toDate()));
        verifyZeroInteractions(futureJobService);
    }

    @Test
    public void it_should_not_schedule_expired_future_jobs_and_change_status_to_EXPIRED() {
        //given
        FutureJob futureJob = FutureJobBuilder.aFutureJob()
                .futureJobStatus(FutureJobStatus.WAITING)
                .startTime(DateUtils.toDateTime("20/12/2013 15:55").toDate())
                .expireTime(DateUtils.toDateTime("31/12/2013 15:55").toDate())
                .build();

        //when
        futureJobSchedulerService.schedule(futureJob);

        //then
        verify(futureJobService).save(futureJobArgumentCaptor.capture());
        final FutureJob expiredFeatureJob = futureJobArgumentCaptor.getValue();
        assertThat(expiredFeatureJob.getFutureJobStatus()).isEqualTo(FutureJobStatus.EXPIRED);
        verifyNoMoreInteractions(futureJobService);
        verifyZeroInteractions(futureJobTaskScheduler);
    }
}