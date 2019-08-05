package com.trendyol.scheduler.service;

import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJobExecutionHistory;
import com.trendyol.scheduler.domain.enums.JobExecutionStatus;
import com.trendyol.scheduler.repository.ScheduledJobExecutionHistoryRepository;
import com.trendyol.scheduler.utils.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduledJobExecutionHistoryService {

    private final ScheduledJobExecutionHistoryRepository scheduledJobExecutionHistoryRepository;

    @Autowired
    public ScheduledJobExecutionHistoryService(ScheduledJobExecutionHistoryRepository scheduledJobExecutionHistoryRepository) {
        this.scheduledJobExecutionHistoryRepository = scheduledJobExecutionHistoryRepository;
    }

    @Transactional
    public void save(ScheduledJobExecutionHistory scheduledJobExecutionHistory) {
        scheduledJobExecutionHistoryRepository.save(scheduledJobExecutionHistory);
    }

    @Transactional
    public Optional<ScheduledJobExecutionHistory> getExecutionHistory(String taskId, JobExecutionStatus jobExecutionStatus) {
        return scheduledJobExecutionHistoryRepository.findByIdAndJobExecutionStatus(taskId, jobExecutionStatus);
    }

    @Transactional
    public Optional<ScheduledJobExecutionHistory> getExecutionHistory(ScheduledJob scheduledJob, JobExecutionStatus jobExecutionStatus) {
        return scheduledJobExecutionHistoryRepository.findByScheduledJobAndJobExecutionStatus(scheduledJob, jobExecutionStatus);
    }

    public List<ScheduledJobExecutionHistory> getStaleInProgressScheduledJobs() {
        Date startOfYesterday = Clock.now().minusDays(1).withTimeAtStartOfDay().toDate();
        return scheduledJobExecutionHistoryRepository.findAllByJobExecutionStatusAndStartDateIsBefore(
                JobExecutionStatus.In_Progress, startOfYesterday
        );
    }
}
