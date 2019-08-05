package com.trendyol.scheduler.service.futurejob;

import com.trendyol.scheduler.builder.FutureJobBuilder;
import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.domain.enums.FutureJobStatus;
import com.trendyol.scheduler.repository.FutureJobRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FutureJobServiceTest {

    @InjectMocks
    private FutureJobService futureJobService;

    @Mock
    private FutureJobRepository futureJobRepository;

    @Test
    public void it_should_save_future_job() {
        //given
        FutureJob futureJob = FutureJobBuilder.aFutureJob().build();
        FutureJob savedFutureJob = FutureJobBuilder.aFutureJob().id(10L).build();
        when(futureJobRepository.save(futureJob)).thenReturn(savedFutureJob);

        //when
        FutureJob actualFutureJob = futureJobService.save(futureJob);

        //then
        verify(futureJobRepository).save(futureJob);
        assertThat(actualFutureJob).isEqualTo(savedFutureJob);
    }

    @Test
    public void it_should_get_waiting_future_jobs() {
        //given
        final FutureJob futureJob1 = FutureJobBuilder.aFutureJob().build();
        final FutureJob futureJob2 = FutureJobBuilder.aFutureJob().build();
        when(futureJobRepository.getFutureJobsByFutureJobStatus(FutureJobStatus.WAITING)).thenReturn(Arrays.asList(futureJob1, futureJob2));

        //when
        final List<FutureJob> waitingFutureJobs = futureJobService.getWaitingFutureJobs();

        //then
        assertThat(waitingFutureJobs).containsExactly(futureJob1, futureJob2);

        verify(futureJobRepository).getFutureJobsByFutureJobStatus(FutureJobStatus.WAITING);
        verifyNoMoreInteractions(futureJobRepository);
    }

    @Test
    public void it_should_get_waiting_future_jobs_by_hash_key() {
        //given
        final FutureJob futureJob1 = FutureJobBuilder.aFutureJob().build();
        final FutureJob futureJob2 = FutureJobBuilder.aFutureJob().build();
        when(futureJobRepository.getFutureJobsByFutureJobStatusAndHashKey(FutureJobStatus.WAITING, "hash-key"))
                .thenReturn(Arrays.asList(futureJob1, futureJob2));

        //when
        final List<FutureJob> waitingFutureJobs = futureJobService.getWaitingFutureJobsByHashKey("hash-key");

        //then
        assertThat(waitingFutureJobs).containsExactly(futureJob1, futureJob2);

        verify(futureJobRepository).getFutureJobsByFutureJobStatusAndHashKey(FutureJobStatus.WAITING, "hash-key");
        verifyNoMoreInteractions(futureJobRepository);
    }

    @Test
    public void it_should_find_future_job_by_id() {
        //Given
        FutureJob futureJob = FutureJobBuilder.aFutureJob().build();
        when(futureJobRepository.findById(1L)).thenReturn(Optional.of(futureJob));

        //When
        Optional<FutureJob> foundFutureJob = futureJobService.findById(1L);

        //Then
        assertThat(foundFutureJob.isPresent()).isTrue();
        assertThat(foundFutureJob.get()).isEqualTo(futureJob);
    }
}