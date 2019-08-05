package com.trendyol.scheduler.domain.entity;

import com.trendyol.scheduler.builder.FutureJobBuilder;
import org.junit.Test;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseEntityTest {

    @Test
    public void it_should_return_false_when_other_entity_is_null() {
        //Given
        BaseEntity baseEntity = new BaseEntity() {
            @Override
            public Serializable getId() {
                return 1L;
            }
        };

        //When
        boolean comparisonResult = baseEntity.equals(null);

        //Then
        assertThat(comparisonResult).isFalse();
    }

    @Test
    public void it_should_return_true_when_two_entities_are_same() {
        //Given
        BaseEntity baseEntity1 = FutureJobBuilder.aFutureJob().id(1L).build();
        BaseEntity baseEntity2 = FutureJobBuilder.aFutureJob().id(1L).build();

        //When
        boolean comparisonResult = baseEntity1.equals(baseEntity2);

        //Then
        assertThat(comparisonResult).isTrue();
    }
}