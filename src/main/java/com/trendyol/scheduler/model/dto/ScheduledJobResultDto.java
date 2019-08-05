package com.trendyol.scheduler.model.dto;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static java.util.Arrays.deepToString;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseStackTrace;

public class ScheduledJobResultDto {

    private static final int ERROR_DETAIL_MAX_LENGTH = 4096;

    private String taskId;
    private boolean success;
    private String message;

    public static ScheduledJobResultDto success(String taskId) {
        ScheduledJobResultDto scheduledJobResultDto = new ScheduledJobResultDto();
        scheduledJobResultDto.setTaskId(taskId);
        scheduledJobResultDto.setSuccess(true);
        return scheduledJobResultDto;
    }

    public static ScheduledJobResultDto failed(String taskId, int httpStatus, Exception exception) {
        String responseFormat = "HttpStatus: %s - Detail: %s";
        ScheduledJobResultDto scheduledJobResultDto = new ScheduledJobResultDto();
        scheduledJobResultDto.setTaskId(taskId);
        scheduledJobResultDto.setSuccess(false);
        String errorMessage = String.format(responseFormat, httpStatus, deepToString(getRootCauseStackTrace(exception)));
        scheduledJobResultDto.setMessage(StringUtils.substring(errorMessage, 0, ERROR_DETAIL_MAX_LENGTH));
        return scheduledJobResultDto;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("taskId", taskId)
                .append("success", success)
                .toString();
    }
}
