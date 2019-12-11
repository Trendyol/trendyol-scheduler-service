package com.trendyol.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"scheduler-service.synchronizer.type=couchbase"})
public class SchedulerServiceApplicationTest {

    @Test
    public void contextLoads() {
    }
}