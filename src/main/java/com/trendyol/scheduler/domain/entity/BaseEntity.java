package com.trendyol.scheduler.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public abstract class BaseEntity implements Serializable {

    public abstract Serializable getId();

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (getId() == null || ((BaseEntity) other).getId() == null) {
            return false;
        }
        return Objects.equals(getId(), ((BaseEntity) other).getId());
    }
}
