package com.trendyol.scheduler.repository;

import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJobExecutionHistory;
import com.trendyol.scheduler.domain.enums.JobExecutionStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ScheduledJobExecutionHistoryRepository extends CrudRepository<ScheduledJobExecutionHistory, String> {

    Optional<ScheduledJobExecutionHistory> findByIdAndJobExecutionStatus(String taskId, JobExecutionStatus jobExecutionStatus);

    Optional<ScheduledJobExecutionHistory> findByScheduledJobAndJobExecutionStatus(ScheduledJob scheduledJob, JobExecutionStatus jobExecutionStatus);

    List<ScheduledJobExecutionHistory> findAllByJobExecutionStatusAndStartDateIsBefore(JobExecutionStatus jobExecutionStatus, Date startDate);
}
