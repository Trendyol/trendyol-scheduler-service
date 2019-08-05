package com.trendyol.scheduler.repository;

import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.domain.enums.FutureJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FutureJobRepository extends JpaRepository<FutureJob, Long> {

    List<FutureJob> getFutureJobsByFutureJobStatus(FutureJobStatus futureJobStatus);

    List<FutureJob> getFutureJobsByFutureJobStatusAndHashKey(FutureJobStatus futureJobStatus, String hashKey);
}
