package com.trendyol.scheduler.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerFutureJobQueueConfiguration {

    private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    private static final String TRENDYOL_SCHEDULER_FUTURE_JOB_EXCHANGE_NAME = "trendyol.scheduler.future.job";
    public static final String TRENDYOL_SCHEDULER_FUTURE_JOB_QUEUE_NAME = "trendyol.scheduler.future.job";
    private static final String TRENDYOL_SCHEDULER_FUTURE_JOB_DEAD_LETTER_NAME = "trendyol.scheduler.future.job.dead-letter";

    @Bean
    public FanoutExchange schedulerFutureJobExchange(AmqpAdmin defaultAdmin) {
        FanoutExchange fanoutExchange = new FanoutExchange(TRENDYOL_SCHEDULER_FUTURE_JOB_EXCHANGE_NAME);
        fanoutExchange.setAdminsThatShouldDeclare(defaultAdmin);
        fanoutExchange.setShouldDeclare(true);
        return fanoutExchange;
    }

    @Bean
    public Queue schedulerFutureJobQueue(AmqpAdmin defaultAdmin) {
        Queue queue = QueueBuilder.durable(TRENDYOL_SCHEDULER_FUTURE_JOB_QUEUE_NAME)
                .withArgument(X_DEAD_LETTER_EXCHANGE, StringUtils.EMPTY)
                .withArgument(X_DEAD_LETTER_ROUTING_KEY, TRENDYOL_SCHEDULER_FUTURE_JOB_DEAD_LETTER_NAME)
                .build();
        queue.setAdminsThatShouldDeclare(defaultAdmin);
        queue.setShouldDeclare(true);
        return queue;
    }

    @Bean
    public Queue schedulerFutureJobDeadLetterQueue(AmqpAdmin defaultAdmin) {
        Queue deadLetterQueue = QueueBuilder.durable(TRENDYOL_SCHEDULER_FUTURE_JOB_DEAD_LETTER_NAME).build();
        deadLetterQueue.setAdminsThatShouldDeclare(defaultAdmin);
        deadLetterQueue.setShouldDeclare(true);
        return deadLetterQueue;
    }

    @Bean
    public Binding schedulerFutureJobBinding(Queue schedulerFutureJobQueue, FanoutExchange schedulerFutureJobExchange, AmqpAdmin defaultAdmin) {
        Binding binding = BindingBuilder.bind(schedulerFutureJobQueue).to(schedulerFutureJobExchange);
        binding.setAdminsThatShouldDeclare(defaultAdmin);
        binding.setShouldDeclare(true);
        return binding;
    }
}
