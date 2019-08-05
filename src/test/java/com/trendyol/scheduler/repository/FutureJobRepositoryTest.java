package com.trendyol.scheduler.repository;

import com.trendyol.scheduler.domain.FutureJob;
import com.trendyol.scheduler.domain.enums.FutureJobStatus;
import com.trendyol.scheduler.utils.Clock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.trendyol.scheduler.builder.FutureJobBuilder.aFutureJob;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@RunWith(SpringRunner.class)
public class FutureJobRepositoryTest {

    @Autowired
    private FutureJobRepository futureJobRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void it_should_get_by_status() {
        //given
        final FutureJob waitingFutureJob = aFutureJob()
                .futureJobStatus(FutureJobStatus.WAITING)
                .name("name")
                .startTime(Clock.now().toDate())
                .application("application")
                .method("POST")
                .path("/path")
                .payload("payload")
                .hashKey("hash")
                .url("url")
                .build();

        final FutureJob expiredFutureJob = aFutureJob()
                .futureJobStatus(FutureJobStatus.EXPIRED)
                .name("name")
                .startTime(Clock.now().toDate())
                .application("application")
                .method("POST")
                .path("/path")
                .payload("payload")
                .hashKey("hash")
                .url("url")
                .build();

        final FutureJob cancelledFutureJob = aFutureJob()
                .futureJobStatus(FutureJobStatus.CANCELLED)
                .name("name")
                .startTime(Clock.now().toDate())
                .application("application")
                .method("POST")
                .path("/path")
                .payload("payload")
                .hashKey("hash")
                .url("url")
                .build();

        final FutureJob successFutureJob = aFutureJob()
                .futureJobStatus(FutureJobStatus.SUCCESS)
                .name("name")
                .startTime(Clock.now().toDate())
                .application("application")
                .method("POST")
                .path("/path")
                .payload("payload")
                .hashKey("hash")
                .url("url")
                .build();

        final FutureJob failedFutureJob = aFutureJob()
                .futureJobStatus(FutureJobStatus.FAILED)
                .name("name")
                .startTime(Clock.now().toDate())
                .application("application")
                .method("POST")
                .path("/path")
                .payload("payload")
                .hashKey("hash")
                .url("url")
                .build();

        testEntityManager.persist(waitingFutureJob);
        testEntityManager.persist(expiredFutureJob);
        testEntityManager.persist(cancelledFutureJob);
        testEntityManager.persist(successFutureJob);
        testEntityManager.persist(failedFutureJob);
        testEntityManager.flush();

        //when
        final List<FutureJob> futureJobsByFutureJobStatus = futureJobRepository.getFutureJobsByFutureJobStatus(FutureJobStatus.SUCCESS);

        //then
        assertThat(futureJobsByFutureJobStatus).containsExactly(successFutureJob);
    }

    @Test
    public void it_should_get_by_status_and_hash_key() {
        //given
        final FutureJob waitingFutureJob1 = aFutureJob()
                .futureJobStatus(FutureJobStatus.WAITING)
                .name("name")
                .startTime(Clock.now().toDate())
                .application("application")
                .method("POST")
                .path("/path")
                .payload("payload")
                .hashKey("hash1")
                .url("url")
                .build();

        final FutureJob waitingFutureJob2 = aFutureJob()
                .futureJobStatus(FutureJobStatus.WAITING)
                .name("name")
                .startTime(Clock.now().toDate())
                .application("application")
                .method("POST")
                .path("/path")
                .payload("payload")
                .hashKey("hash2")
                .url("url")
                .build();

        final FutureJob expiredFutureJob = aFutureJob()
                .futureJobStatus(FutureJobStatus.EXPIRED)
                .name("name")
                .startTime(Clock.now().toDate())
                .application("application")
                .method("POST")
                .path("/path")
                .payload("payload")
                .hashKey("hash1")
                .url("url")
                .build();

        final FutureJob cancelledFutureJob = aFutureJob()
                .futureJobStatus(FutureJobStatus.CANCELLED)
                .name("name")
                .startTime(Clock.now().toDate())
                .application("application")
                .method("POST")
                .path("/path")
                .payload("payload")
                .hashKey("hash1")
                .url("url")
                .build();

        final FutureJob successFutureJob = aFutureJob()
                .futureJobStatus(FutureJobStatus.SUCCESS)
                .name("name")
                .startTime(Clock.now().toDate())
                .application("application")
                .method("POST")
                .path("/path")
                .payload("payload")
                .hashKey("hash1")
                .url("url")
                .build();

        final FutureJob failedFutureJob = aFutureJob()
                .futureJobStatus(FutureJobStatus.FAILED)
                .name("name")
                .startTime(Clock.now().toDate())
                .application("application")
                .method("POST")
                .path("/path")
                .payload("payload")
                .hashKey("hash1")
                .url("url")
                .build();

        testEntityManager.persist(waitingFutureJob1);
        testEntityManager.persist(waitingFutureJob2);
        testEntityManager.persist(expiredFutureJob);
        testEntityManager.persist(cancelledFutureJob);
        testEntityManager.persist(successFutureJob);
        testEntityManager.persist(failedFutureJob);
        testEntityManager.flush();

        //when
        final List<FutureJob> futureJobsByFutureJobStatus = futureJobRepository.getFutureJobsByFutureJobStatusAndHashKey(FutureJobStatus.WAITING, "hash1");

        //then
        assertThat(futureJobsByFutureJobStatus).containsExactly(waitingFutureJob1);
    }
}