package com.hrpayroll.domain.model.payroll;

import com.hrpayroll.domain.shared.ValueObject;

import java.util.Objects;
import java.util.UUID;

public class PayrollId implements ValueObject {
    private final UUID value;

    private PayrollId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Payroll ID cannot be null");
        }
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    public static PayrollId of(UUID value) {
        return new PayrollId(value);
    }

    public static PayrollId generate() {
        return new PayrollId(UUID.randomUUID());
    }

    public static PayrollId fromString(String value) {
        return new PayrollId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayrollId that = (PayrollId) o;
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
