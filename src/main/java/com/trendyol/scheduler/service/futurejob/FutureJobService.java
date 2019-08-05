package com.trendyol.scheduler.service.futurejob;

import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.domain.enums.FutureJobStatus;
import com.trendyol.scheduler.repository.FutureJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FutureJobService {

    private final FutureJobRepository futureJobRepository;

    @Autowired
    public FutureJobService(FutureJobRepository futureJobRepository) {
        this.futureJobRepository = futureJobRepository;
    }

    @Transactional
    public FutureJob save(FutureJob futureJob) {
        return futureJobRepository.save(futureJob);
    }

    public List<FutureJob> getWaitingFutureJobs() {
        return futureJobRepository.getFutureJobsByFutureJobStatus(FutureJobStatus.WAITING);
    }

    public List<FutureJob> getWaitingFutureJobsByHashKey(String hashKey) {
        return futureJobRepository.getFutureJobsByFutureJobStatusAndHashKey(FutureJobStatus.WAITING, hashKey);
    }

    public Optional<FutureJob> findById(Long id) {
        return futureJobRepository.findById(id);
    }
}
