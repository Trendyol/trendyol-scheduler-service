package com.trendyol.scheduler.service.futurejob;

import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.model.request.FutureJobRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.trendyol.scheduler.configuration.SchedulerFutureJobQueueConfiguration.TRENDYOL_SCHEDULER_FUTURE_JOB_QUEUE_NAME;

@Service
public class FutureJobListenerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureJobListenerService.class);

    private final FutureJobCreatorService futureJobCreatorService;
    private final FutureJobSchedulerService futureJobSchedulerService;

    @Autowired
    public FutureJobListenerService(FutureJobCreatorService futureJobCreatorService,
                                    FutureJobSchedulerService futureJobSchedulerService) {
        this.futureJobCreatorService = futureJobCreatorService;
        this.futureJobSchedulerService = futureJobSchedulerService;
    }

    @RabbitListener(queues = TRENDYOL_SCHEDULER_FUTURE_JOB_QUEUE_NAME)
    public void scheduleFutureJob(FutureJobRequest futureJobRequest) {
        try {
            FutureJob futureJob = futureJobCreatorService.createFutureJob(futureJobRequest);
            futureJobSchedulerService.schedule(futureJob);
            LOGGER.debug("Future Job scheduled. Future job : {}", futureJob);
        } catch (Exception e) {
            LOGGER.error("Future job scheduling error occurred.", e);
            throw e;
        }
    }
}
