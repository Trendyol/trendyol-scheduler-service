package com.trendyol.scheduler.service.futurejob;

import com.trendyol.scheduler.constants.AuditionConstants;
import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.domain.enums.FutureJobStatus;
import com.trendyol.scheduler.model.request.FutureJobRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FutureJobCreatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureJobCreatorService.class);

    private final FutureJobService futureJobService;

    @Autowired
    public FutureJobCreatorService(FutureJobService futureJobService) {
        this.futureJobService = futureJobService;
    }

    @Transactional
    public FutureJob createFutureJob(FutureJobRequest futureJobRequest) {
        if (futureJobRequest.isSingleSchedulingType()) {
            List<FutureJob> waitingFutureJobsByHashKey = futureJobService.getWaitingFutureJobsByHashKey(futureJobRequest.getRequestUniqueKey());
            waitingFutureJobsByHashKey.forEach(futureJob -> {
                futureJob.setFutureJobStatus(FutureJobStatus.CANCELLED);
                futureJobService.save(futureJob);
                LOGGER.debug("Future job has been cancelled. FutureJob : {}", futureJob);
            });
        }
        FutureJob futureJob = buildFutureJob(futureJobRequest);
        return futureJobService.save(futureJob);
    }

    private FutureJob buildFutureJob(FutureJobRequest futureJobRequest) {
        FutureJob futureJob = new FutureJob();
        futureJob.setHashKey(futureJobRequest.getRequestUniqueKey());
        futureJob.setFutureJobStatus(FutureJobStatus.WAITING);
        futureJob.setTaskId(MDC.get(AuditionConstants.X_CORRELATION_ID));
        futureJob.setApplication(futureJobRequest.getApplication());
        futureJob.setName(futureJobRequest.getName());
        futureJob.setStartTime(futureJobRequest.getStartDate());
        futureJob.setExpireTime(futureJobRequest.getExpireDate());
        futureJob.setPath(futureJobRequest.getPath());
        futureJob.setMethod(futureJobRequest.getMethod());
        futureJob.setPayload(futureJobRequest.getPayload());
        return futureJob;
    }
}
