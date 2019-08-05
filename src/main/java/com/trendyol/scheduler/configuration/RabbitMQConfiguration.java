package com.trendyol.scheduler.configuration;

import com.trendyol.scheduler.constants.AuditionConstants;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.MDC;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.UUID;

import static com.trendyol.scheduler.constants.AuditionConstants.UNKNOWN_APPLICATION;
import static com.trendyol.scheduler.constants.AuditionConstants.X_AGENTNAME;
import static com.trendyol.scheduler.constants.AuditionConstants.X_CORRELATION_ID;
import static com.trendyol.scheduler.constants.AuditionConstants.X_EXECUTOR_USER;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Configuration
public class RabbitMQConfiguration {

    private static final int ARGUMENT_MESSAGE_INDEX = 1;
    private static final String BRACKET_OPEN = "[";
    private static final String BRACKET_CLOSE = "]";

    private static final MethodInterceptor METHOD_INTERCEPTOR = invocation -> {
        Message message = (Message) invocation.getArguments()[ARGUMENT_MESSAGE_INDEX];
        MDC.put(X_CORRELATION_ID, retrieveCorrelationFromMessage(message));
        MDC.put(AuditionConstants.X_AGENTNAME, retrieveAgentFromMessage(message));
        MDC.put(X_EXECUTOR_USER, retrieveExecutorUserFromMessage(message));
        return invocation.proceed();
    };

    private final RabbitProperties rabbitProperties;

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    public RabbitMQConfiguration(RabbitProperties rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            @Qualifier("defaultConnectionFactory") ConnectionFactory defaultConnectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, defaultConnectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAdviceChain(retryAdvice(), METHOD_INTERCEPTOR);
        return factory;
    }

    @Bean
    public RetryOperationsInterceptor retryAdvice() {
        RabbitProperties.ListenerRetry retryConfig = rabbitProperties.getListener().getDirect().getRetry();
        return RetryInterceptorBuilder
                .stateless()
                .maxAttempts(retryConfig.getMaxAttempts())
                .backOffOptions(
                        retryConfig.getInitialInterval().toMillis(),
                        retryConfig.getMultiplier(),
                        retryConfig.getMaxInterval().toMillis()
                )
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

    @Bean
    public ConnectionFactory defaultConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(rabbitProperties.getHost());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setConnectionNameStrategy(connFactory -> applicationName);
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin defaultAdmin(ConnectionFactory defaultConnectionFactory) {
        return new RabbitAdmin(defaultConnectionFactory);
    }

    private static String retrieveAgentFromMessage(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        if (isNull(messageProperties)) {
            return UNKNOWN_APPLICATION;
        }
        String agentName = (String) messageProperties.getHeaders().get(X_AGENTNAME);
        if (isBlank(agentName)) {
            String queueName = messageProperties.getConsumerQueue();
            return UNKNOWN_APPLICATION + (isNotBlank(queueName) ? BRACKET_OPEN + queueName + BRACKET_CLOSE : EMPTY);
        }
        return agentName;
    }

    private static String retrieveCorrelationFromMessage(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        if (isNull(messageProperties)) {
            return UUID.randomUUID().toString();
        }
        String correlationId = (String) messageProperties.getHeaders().getOrDefault(X_CORRELATION_ID, EMPTY);
        if (isBlank(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }
        return correlationId;
    }

    private static String retrieveExecutorUserFromMessage(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        if (isNull(messageProperties)) {
            return EMPTY;
        }
        return (String) messageProperties.getHeaders().getOrDefault(X_EXECUTOR_USER, EMPTY);
    }
}
