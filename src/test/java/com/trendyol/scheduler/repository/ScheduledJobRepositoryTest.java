package com.trendyol.scheduler.repository;

import com.trendyol.scheduler.builder.domain.BeanScheduledJobBuilder;
import com.trendyol.scheduler.builder.domain.RestScheduledJobBuilder;
import com.trendyol.scheduler.domain.BeanScheduledJob;
import com.trendyol.scheduler.domain.RestScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ScheduledJobRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ScheduledJobRepository scheduledJobRepository;

    @Test
    public void it_should_load_active_scheduled_jobs() {
        //Given
        BeanScheduledJob scheduledJob1 = BeanScheduledJobBuilder.aBeanScheduledJob()
                .active(true)
                .application("application1")
                .beanName("bean")
                .cronExpression("* * * * *")
                .methodName("method")
                .name("name1")
                .url("url")
                .build();

        BeanScheduledJob scheduledJob2 = BeanScheduledJobBuilder.aBeanScheduledJob()
                .active(true)
                .application("application2")
                .beanName("bean")
                .cronExpression("* * * * *")
                .methodName("method")
                .name("name2")
                .url("url")
                .build();

        BeanScheduledJob scheduledJob3 = BeanScheduledJobBuilder.aBeanScheduledJob()
                .active(false)
                .application("application3")
                .beanName("bean")
                .cronExpression("* * * * *")
                .methodName("method")
                .name("name")
                .url("url")
                .build();

        RestScheduledJob restScheduledJob = RestScheduledJobBuilder.aRestScheduledJob()
                .active(true)
                .application("application1")
                .path("path")
                .cronExpression("* * * * *")
                .method("GET")
                .payload("")
                .name("restname")
                .url("url")
                .build();

        testEntityManager.persistAndFlush(scheduledJob1);
        testEntityManager.persistAndFlush(scheduledJob2);
        testEntityManager.persistAndFlush(scheduledJob3);
        testEntityManager.persistAndFlush(restScheduledJob);

        //When
        List<ScheduledJob> found = scheduledJobRepository.findAllByActive(true);

        //Then
        assertThat(found).hasSize(3);
        assertThat(found.stream().map(ScheduledJob::getName).collect(Collectors.toList())).contains(scheduledJob1.getName(), scheduledJob2.getName(),
                restScheduledJob.getName());
    }
}