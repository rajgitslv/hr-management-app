package com.hrpayroll.domain.model.department;

import com.hrpayroll.domain.shared.ValueObject;

import java.util.Objects;
import java.util.UUID;

public class DepartmentId implements ValueObject {
    private final UUID value;

    private DepartmentId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Department ID cannot be null");
        }
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    public static DepartmentId of(UUID value) {
        return new DepartmentId(value);
    }

    public static DepartmentId generate() {
        return new DepartmentId(UUID.randomUUID());
    }

    public static DepartmentId fromString(String value) {
        return new DepartmentId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentId that = (DepartmentId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
