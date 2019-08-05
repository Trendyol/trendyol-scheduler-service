package com.trendyol.scheduler.domain.entity;

import com.trendyol.scheduler.FrozenClock;
import com.trendyol.scheduler.utils.Clock;
import org.junit.Rule;
import org.junit.Test;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;

public class AuditingEntityTest {

    @Rule
    public FrozenClock.Rule frozenClock = new FrozenClock.Rule();

    @Test
    @FrozenClock("01/01/2019")
    public void it_should_set_last_modified_date_as_now_on_pre_update() {
        //Given
        AuditingEntity auditingEntity = new AuditingEntity() {
            @Override
            public Serializable getId() {
                return 1L;
            }
        };

        //When
        auditingEntity.preUpdate();

        //Then
        assertThat(auditingEntity.getLastModifiedDate()).isEqualTo(Clock.now().toDate());
    }
}