package com.trendyol.scheduler.configuration;

import org.junit.Test;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static org.assertj.core.api.Assertions.assertThat;

public class SchedulerConfigTest {

    @Test
    public void it_should_create_thread_pool_task_scheduler() {
        //Given
        SchedulerConfig schedulerConfig = new SchedulerConfig();

        //When
        TaskScheduler taskScheduler = schedulerConfig.taskScheduler();
        //Then
        assertThat(taskScheduler).isInstanceOf(ThreadPoolTaskScheduler.class);
    }
}