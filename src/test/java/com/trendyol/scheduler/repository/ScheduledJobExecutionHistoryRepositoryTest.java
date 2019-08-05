package com.trendyol.scheduler.repository;

import com.trendyol.scheduler.builder.domain.RestScheduledJobBuilder;
import com.trendyol.scheduler.builder.domain.ScheduledJobExecutionHistoryBuilder;
import com.trendyol.scheduler.domain.ScheduledJob;
import com.trendyol.scheduler.domain.ScheduledJobExecutionHistory;
import com.trendyol.scheduler.domain.enums.JobExecutionStatus;
import com.trendyol.scheduler.utils.Clock;
import com.trendyol.scheduler.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ScheduledJobExecutionHistoryRepositoryTest {

    @Autowired
    private ScheduledJobExecutionHistoryRepository scheduledJobExecutionHistoryRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void it_should_return_scheduled_job_history_by_id_and_execution_status() {
        //given
        ScheduledJobExecutionHistory scheduledJobExecutionHistoryData1 = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .id("taskId1")
                .jobExecutionStatus(JobExecutionStatus.In_Progress)
                .startDate(Clock.now().toDate())
                .build();
        ScheduledJobExecutionHistory scheduledJobExecutionHistoryData2 = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .id("taskId2")
                .jobExecutionStatus(JobExecutionStatus.Failed)
                .startDate(Clock.now().toDate())
                .build();
        ScheduledJobExecutionHistory scheduledJobExecutionHistoryData3 = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .id("taskId3")
                .jobExecutionStatus(JobExecutionStatus.In_Progress)
                .startDate(Clock.now().toDate())
                .build();
        testEntityManager.persist(scheduledJobExecutionHistoryData1);
        testEntityManager.persist(scheduledJobExecutionHistoryData2);
        testEntityManager.persist(scheduledJobExecutionHistoryData3);

        //when
        Optional<ScheduledJobExecutionHistory> result = scheduledJobExecutionHistoryRepository.findByIdAndJobExecutionStatus("taskId1", JobExecutionStatus.In_Progress);

        //then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("taskId1");
    }

    @Test
    public void it_should_return_scheduled_job_history_by_scheduled_job_and_execution_status() {
        //given
        ScheduledJob scheduledJob1 = RestScheduledJobBuilder.aRestScheduledJob()
                .name("job-name-1")
                .cronExpression("* * * * * *")
                .active(true)
                .application("application1")
                .url("url")
                .build();
        ScheduledJob scheduledJob2 = RestScheduledJobBuilder.aRestScheduledJob()
                .name("job-name-2")
                .cronExpression("* * * * * *")
                .active(true)
                .application("application2")
                .url("url")
                .build();
        ScheduledJob scheduledJob3 = RestScheduledJobBuilder.aRestScheduledJob()
                .name("job-name-3")
                .cronExpression("* * * * * *")
                .active(true)
                .application("application3")
                .url("url")
                .build();
        testEntityManager.persist(scheduledJob1);
        testEntityManager.persist(scheduledJob2);
        testEntityManager.persist(scheduledJob3);

        testEntityManager.flush();

        ScheduledJobExecutionHistory scheduledJobExecutionHistoryData1 = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .id("taskId1")
                .jobExecutionStatus(JobExecutionStatus.In_Progress)
                .startDate(Clock.now().toDate())
                .scheduledJob(scheduledJob1)
                .build();
        ScheduledJobExecutionHistory scheduledJobExecutionHistoryData2 = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .id("taskId2")
                .jobExecutionStatus(JobExecutionStatus.Failed)
                .startDate(Clock.now().toDate())
                .scheduledJob(scheduledJob2)
                .build();
        ScheduledJobExecutionHistory scheduledJobExecutionHistoryData3 = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .id("taskId3")
                .jobExecutionStatus(JobExecutionStatus.In_Progress)
                .startDate(Clock.now().toDate())
                .scheduledJob(scheduledJob3)
                .build();
        testEntityManager.persist(scheduledJobExecutionHistoryData1);
        testEntityManager.persist(scheduledJobExecutionHistoryData2);
        testEntityManager.persist(scheduledJobExecutionHistoryData3);

        //when
        Optional<ScheduledJobExecutionHistory> result = scheduledJobExecutionHistoryRepository.findByScheduledJobAndJobExecutionStatus(scheduledJob1, JobExecutionStatus.In_Progress);

        //then
        assertThat(result).isPresent();
        ScheduledJobExecutionHistory foundHistory = result.get();
        assertThat(foundHistory.getId()).isEqualTo("taskId1");
        assertThat(foundHistory.getScheduledJob().getName()).isEqualTo("job-name-1");
    }

    @Test
    public void it_should_return_scheduled_job_history_by_id_and_execution_statuss() {
        //given
        ScheduledJobExecutionHistory scheduledJobExecutionHistoryData1 = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .id("taskId1")
                .jobExecutionStatus(JobExecutionStatus.In_Progress)
                .startDate(DateUtils.toDateTime("11/12/2013 14:14").toDate())
                .build();
        ScheduledJobExecutionHistory scheduledJobExecutionHistoryData2 = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .id("taskId2")
                .jobExecutionStatus(JobExecutionStatus.Failed)
                .startDate(DateUtils.toDateTime("11/12/2013 14:14").toDate())
                .build();
        ScheduledJobExecutionHistory scheduledJobExecutionHistoryData3 = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .id("taskId3")
                .jobExecutionStatus(JobExecutionStatus.In_Progress)
                .startDate(DateUtils.toDateTime("11/12/2013 14:15").toDate())
                .build();
        ScheduledJobExecutionHistory scheduledJobExecutionHistoryData4 = ScheduledJobExecutionHistoryBuilder.aScheduledJobExecutionHistory()
                .id("taskId4")
                .jobExecutionStatus(JobExecutionStatus.In_Progress)
                .startDate(DateUtils.toDateTime("10/12/2013 20:00").toDate())
                .build();
        testEntityManager.persist(scheduledJobExecutionHistoryData1);
        testEntityManager.persist(scheduledJobExecutionHistoryData2);
        testEntityManager.persist(scheduledJobExecutionHistoryData3);
        testEntityManager.persist(scheduledJobExecutionHistoryData4);
        testEntityManager.flush();

        //when
        List<ScheduledJobExecutionHistory> foundStaleJobs = scheduledJobExecutionHistoryRepository.findAllByJobExecutionStatusAndStartDateIsBefore(
                JobExecutionStatus.In_Progress,
                DateUtils.toDateTime("11/12/2013 14:15").toDate()
        );

        //then
        assertThat(foundStaleJobs).hasSize(2);
        assertThat(foundStaleJobs).extracting("id").containsOnlyOnce("taskId1", "taskId4");
    }
}