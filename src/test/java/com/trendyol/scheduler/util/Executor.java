package com.trendyol.scheduler.util;

import com.trendyol.scheduler.constants.AuditionConstants;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Executor {

    String executorId();

    String executorApp();

    String executorUser();

    class Rule implements TestRule {

        @Override
        public Statement apply(Statement statement, Description description) {
            Executor annotation = description.getAnnotation(Executor.class);
            if (annotation == null) {
                return statement;
            }
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    try {
                        MDC.put(AuditionConstants.X_CORRELATION_ID, annotation.executorId());
                        MDC.put(AuditionConstants.X_AGENTNAME, annotation.executorApp());
                        MDC.put(AuditionConstants.X_EXECUTOR_USER, annotation.executorUser());

                        MockHttpServletRequest request = new MockHttpServletRequest();
                        request.addHeader(AuditionConstants.X_CORRELATION_ID, annotation.executorId());
                        request.addHeader(AuditionConstants.X_AGENTNAME, annotation.executorApp());
                        request.addHeader(AuditionConstants.X_EXECUTOR_USER, annotation.executorUser());
                        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
                        statement.evaluate();
                    } finally {
                        RequestContextHolder.resetRequestAttributes();
                        MDC.clear();
                    }
                }
            };
        }

    }

}
