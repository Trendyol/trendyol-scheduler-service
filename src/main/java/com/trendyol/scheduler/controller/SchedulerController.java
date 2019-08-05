package com.trendyol.scheduler.controller;

import com.trendyol.scheduler.model.ExecutionContext;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Optional;

@RestController
@RequestMapping("scheduler")
@Api(hidden = true)
public class SchedulerController {

    private final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

    private final ApplicationContext applicationContext;

    @Autowired
    public SchedulerController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostMapping("/invoke")
    public ResponseEntity invoke(@RequestBody ExecutionContext executionContext) {
        logger.debug(
                "Scheduler invocation started for bean: {} method: {} and taskId: {}",
                executionContext.getBean(),
                executionContext.getMethod(),
                executionContext.getTaskId()
        );
        Object bean = applicationContext.getBean(executionContext.getBean());
        Method method = ReflectionUtils.findMethod(bean.getClass(), executionContext.getMethod());
        Object result = ReflectionUtils.invokeMethod(method, bean);
        logger.debug(
                "Scheduler invocation finished for bean: {} method: {} and taskId: {}",
                executionContext.getBean(),
                executionContext.getMethod(),
                executionContext.getTaskId()
        );
        return ResponseEntity.accepted().body(getResponseBody(result));
    }

    private String getResponseBody(Object result) {
        return Optional
                .ofNullable(result)
                .orElse(StringUtils.EMPTY)
                .toString();
    }
}
