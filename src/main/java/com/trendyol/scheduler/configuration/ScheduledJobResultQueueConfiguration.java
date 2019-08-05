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
public class ScheduledJobResultQueueConfiguration {

    private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    private static final String EXCHANGE_NAME = "trendyol.scheduledJob.result";
    public static final String QUEUE_NAME = "trendyol.scheduledJob.result";
    private static final String DEAD_LETTER_NAME = "trendyol.scheduledJob.result.dead-letter";

    @Bean
    public FanoutExchange scheduledJobResultExchange(AmqpAdmin defaultAdmin) {
        FanoutExchange fanoutExchange = new FanoutExchange(EXCHANGE_NAME);
        fanoutExchange.setAdminsThatShouldDeclare(defaultAdmin);
        fanoutExchange.setShouldDeclare(true);
        return fanoutExchange;
    }

    @Bean
    public Queue scheduledJobResultQueue(AmqpAdmin defaultAdmin) {
        Queue queue = QueueBuilder.durable(QUEUE_NAME)
                .withArgument(X_DEAD_LETTER_EXCHANGE, StringUtils.EMPTY)
                .withArgument(X_DEAD_LETTER_ROUTING_KEY, DEAD_LETTER_NAME)
                .build();
        queue.setAdminsThatShouldDeclare(defaultAdmin);
        queue.setShouldDeclare(true);
        return queue;
    }

    @Bean
    public Queue scheduledJobResultDeadLetterQueue(AmqpAdmin defaultAdmin) {
        Queue deadLetterQueue = QueueBuilder.durable(DEAD_LETTER_NAME).build();
        deadLetterQueue.setAdminsThatShouldDeclare(defaultAdmin);
        deadLetterQueue.setShouldDeclare(true);
        return deadLetterQueue;
    }

    @Bean
    public Binding scheduledJobResultBinding(Queue scheduledJobResultQueue, FanoutExchange scheduledJobResultExchange, AmqpAdmin defaultAdmin) {
        Binding binding = BindingBuilder.bind(scheduledJobResultQueue).to(scheduledJobResultExchange);
        binding.setAdminsThatShouldDeclare(defaultAdmin);
        binding.setShouldDeclare(true);
        return binding;
    }
}
