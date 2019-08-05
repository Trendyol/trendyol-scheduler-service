package com.trendyol.scheduler.service;

import com.trendyol.scheduler.configuration.ScheduledJobResultQueueConfiguration;
import com.trendyol.scheduler.domain.ScheduledJobExecutionHistory;
import com.trendyol.scheduler.domain.enums.JobExecutionStatus;
import com.trendyol.scheduler.model.dto.ScheduledJobResultDto;
import com.trendyol.scheduler.utils.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.trendyol.scheduler.domain.enums.JobExecutionStatus.Failed;
import static com.trendyol.scheduler.domain.enums.JobExecutionStatus.Success;

@Component
public class ScheduledJobResultListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledJobResultListener.class);

    private final ScheduledJobExecutionHistoryService scheduledJobExecutionHistoryService;

    public ScheduledJobResultListener(ScheduledJobExecutionHistoryService scheduledJobExecutionHistoryService) {
        this.scheduledJobExecutionHistoryService = scheduledJobExecutionHistoryService;
    }

    @RabbitListener(queues = ScheduledJobResultQueueConfiguration.QUEUE_NAME)
    @Transactional
    public void listenScheduledJobResult(ScheduledJobResultDto scheduledJobResultDto) {
        try {
            LOGGER.debug("Scheduled job result process started: {}", scheduledJobResultDto);
            Optional<ScheduledJobExecutionHistory> executionHistory = scheduledJobExecutionHistoryService.getExecutionHistory(
                    scheduledJobResultDto.getTaskId(), JobExecutionStatus.In_Progress
            );
            if (!executionHistory.isPresent()) {
                LOGGER.warn("Not found In_Progress scheduled job execution history for result: {}", scheduledJobResultDto);
                return;
            }
            ScheduledJobExecutionHistory inProgressExecution = executionHistory.get();
            inProgressExecution.setJobExecutionStatus(scheduledJobResultDto.isSuccess() ? Success : Failed);
            inProgressExecution.setErrorDetail(scheduledJobResultDto.getMessage());
            inProgressExecution.setEndDate(Clock.now().toDate());
            scheduledJobExecutionHistoryService.save(inProgressExecution);
            LOGGER.debug("Scheduled job result saved: {}", scheduledJobResultDto);
        } catch (Exception e) {
            LOGGER.error("Error occurred on scheduled job result listener.", e);
            throw e;
        }
    }
}
