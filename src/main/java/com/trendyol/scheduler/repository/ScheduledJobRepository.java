package com.trendyol.scheduler.repository;

import com.trendyol.scheduler.domain.ScheduledJob;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScheduledJobRepository extends CrudRepository<ScheduledJob, Integer> {
    List<ScheduledJob> findAllByActive(boolean active);

}
