package com.trendyol.scheduler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "scheduler-service.synchronizer.type", havingValue = "redis")
public class JobSynchronizeRedisService implements JobSynchronizer {

    private static final long INITIAL_COUNTER_VALUE = 0L;
    private static final int COUNTER_DELTA_VALUE = 1;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public JobSynchronizeRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAssignableToThisExecution(SyncJob job) {
        return getCounter(job) == INITIAL_COUNTER_VALUE;
    }

    private Long getCounter(SyncJob job) {
        Object counter = redisTemplate.opsForValue().get(job.jobKey());

        if (Objects.nonNull(counter)) {
            redisTemplate.expire(job.jobKey(), job.jobTTL(), TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(job.jobKey(), COUNTER_DELTA_VALUE);
            redisTemplate.expire(job.jobKey(), job.jobTTL(), TimeUnit.SECONDS);
            counter = INITIAL_COUNTER_VALUE;
        }

        return (Long) counter;
    }
}

