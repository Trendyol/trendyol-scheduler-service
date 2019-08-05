package com.trendyol.scheduler.service.futurejob;

import com.trendyol.scheduler.constants.AuditionConstants;
import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.domain.enums.FutureJobStatus;
import com.trendyol.scheduler.service.JobExecutorService;
import com.trendyol.scheduler.service.JobSynchronizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FutureJobExecutorService extends JobExecutorService<FutureJob> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureJobExecutorService.class);

    private final RestTemplate restTemplate;
    private final FutureJobService futureJobService;

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    public FutureJobExecutorService(RestTemplate restTemplate,
                                    FutureJobService futureJobService,
                                    JobSynchronizeService jobSynchronizeService) {
        super(jobSynchronizeService);
        this.restTemplate = restTemplate;
        this.futureJobService = futureJobService;
    }

    @Override
    public void process(FutureJob futureJob) {
        boolean isNotWaitingStatus = futureJobService.findById(futureJob.getId())
                .map(FutureJob::isNotWaiting)
                .orElse(false);
        if (isNotWaitingStatus) {
            LOGGER.warn("Future job is not in WAITING status. FutureJob : {}", futureJob);
            return;
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.add(AuditionConstants.X_CORRELATION_ID, futureJob.getTaskId());
        headers.add(AuditionConstants.X_AGENTNAME, applicationName);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    futureJob.getUrl() + futureJob.getPath(),
                    HttpMethod.resolve(futureJob.getMethod()),
                    new HttpEntity<>(futureJob.getPayload(), headers),
                    Void.class
            );
            FutureJobStatus futureJobStatus = response.getStatusCode().is2xxSuccessful() ? FutureJobStatus.SUCCESS : FutureJobStatus.FAILED;
            futureJob.setFutureJobStatus(futureJobStatus);
        } catch (Exception e) {
            LOGGER.error("An exception occurred when executing future job with parameters: " + futureJob, e);
            futureJob.setFutureJobStatus(FutureJobStatus.FAILED);
        }

        futureJobService.save(futureJob);
        LOGGER.debug("Future job finished with parameters: {}", futureJob);
    }

}
