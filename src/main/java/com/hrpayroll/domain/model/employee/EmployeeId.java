package com.hrpayroll.domain.model.employee;

import com.hrpayroll.domain.shared.ValueObject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique employee identifier.
 */
public class EmployeeId implements ValueObject {
    private final UUID value;

    private EmployeeId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    public static EmployeeId of(UUID value) {
        return new EmployeeId(value);
    }

    public static EmployeeId generate() {
        return new EmployeeId(UUID.randomUUID());
    }

    public static EmployeeId fromString(String value) {
        return new EmployeeId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeId that = (EmployeeId) o;
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
