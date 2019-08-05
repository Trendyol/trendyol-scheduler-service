package com.trendyol.scheduler.model.request;

import com.trendyol.scheduler.FrozenClock;
import com.trendyol.scheduler.model.enums.FutureJobSchedulingType;
import com.trendyol.scheduler.utils.Clock;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FutureJobRequestTest {

    @Rule
    public FrozenClock.Rule frozenClockRule = new FrozenClock.Rule();

    @Test
    public void it_should_return_future_job_with_scheduling_type() {
        //Given
        FutureJobRequest futureJobRequest = FutureJobRequest.aSingleFutureJobRequest().name("name");

        //When
        FutureJobRequest futureJobRequestWithSchType = futureJobRequest.futureJobSchedulingType(FutureJobSchedulingType.SINGLE);

        //Then
        assertThat(futureJobRequestWithSchType.getFutureJobSchedulingType()).isEqualTo(FutureJobSchedulingType.SINGLE);
    }

    @Test
    @FrozenClock("01/01/2019")
    public void it_should_convert_to_string_properly() {
        //Given
        FutureJobRequest futureJobRequest = FutureJobRequest.aSingleFutureJobRequest()
                .futureJobSchedulingType(FutureJobSchedulingType.SINGLE)
                .startDate(Clock.now().toDate())
                .expireDate(Clock.now().toDate())
                .name("name")
                .application("application")
                .path("path")
                .method("method")
                .payload("payload");

        //When
        String futureJobRequestStringValue = futureJobRequest.toString();

        //Then
        assertThat(futureJobRequestStringValue).isEqualTo("FutureJobRequest[futureJobSchedulingType=SINGLE," +
                "startDate=Tue Jan 01 03:00:00 EET 2019,expireDate=Tue Jan 01 03:00:00 EET 2019,name=name," +
                "application=application,path=path,method=method,payload=payload]");
    }
}