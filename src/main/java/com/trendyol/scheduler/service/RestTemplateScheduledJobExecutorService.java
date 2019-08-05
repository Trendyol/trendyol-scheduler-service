package com.trendyol.scheduler.service;

import com.trendyol.scheduler.constants.AuditionConstants;
import com.trendyol.scheduler.constants.SchedulerConstants;
import com.trendyol.scheduler.domain.BeanScheduledJob;
import com.trendyol.scheduler.domain.RestScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.model.ExecutionContextModel;
import com.trendyol.scheduler.model.SchedulerServiceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import java.net.ConnectException;
import java.util.concurrent.ExecutionException;

@Service
public class RestTemplateScheduledJobExecutorService {

    private static final String BEAN_RESOURCE = "/scheduler/invoke";

    private final AsyncRestTemplate asyncJobRestTemplate;

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    public RestTemplateScheduledJobExecutorService(AsyncRestTemplate asyncJobRestTemplate) {
        this.asyncJobRestTemplate = asyncJobRestTemplate;
    }

    public void executeWithTaskId(String taskId, ScheduledJob scheduledJob) {
        if (scheduledJob instanceof BeanScheduledJob) {
            BeanScheduledJob beanScheduledJob = (BeanScheduledJob) scheduledJob;
            ExecutionContextModel executionContextModel = new ExecutionContextModel();
            executionContextModel.setBean(beanScheduledJob.getBeanName());
            executionContextModel.setMethod(beanScheduledJob.getMethodName());
            executionContextModel.setTaskId(taskId);
            String url = scheduledJob.getUrl() + BEAN_RESOURCE;

            ListenableFuture<ResponseEntity<String>> futureResponse = asyncJobRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(executionContextModel, prepareHttpHeaders(taskId)),
                    String.class
            );
            handleResponse(futureResponse);
        } else if (scheduledJob instanceof RestScheduledJob) {
            RestScheduledJob restScheduledJob = (RestScheduledJob) scheduledJob;
            String url = scheduledJob.getUrl() + restScheduledJob.getPath();

            ListenableFuture<ResponseEntity<String>> futureResponse = asyncJobRestTemplate.exchange(
                    url,
                    HttpMethod.resolve(restScheduledJob.getMethod()),
                    new HttpEntity<>(restScheduledJob.getPayload(), prepareHttpHeaders(taskId)),
                    String.class
            );
            handleResponse(futureResponse);
        } else {
            throw new SchedulerServiceException(scheduledJob.getClass().getCanonicalName() +
                    " is not valid job type. Updating job configuration can fix this error scheduledJobId " +
                    scheduledJob.getId());
        }
    }

    private void handleResponse(ListenableFuture<ResponseEntity<String>> futureResponse) {
        try {
            futureResponse.get();
        } catch (ExecutionException | InterruptedException e) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if (rootCause instanceof ConnectException) {
                throw new SchedulerServiceException(rootCause);
            }
        }
    }

    private HttpHeaders prepareHttpHeaders(String taskId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(AuditionConstants.X_CORRELATION_ID, taskId);
        headers.add(AuditionConstants.X_AGENTNAME, applicationName);
        headers.add(SchedulerConstants.X_TASK_ID, taskId);
        return headers;
    }

}
