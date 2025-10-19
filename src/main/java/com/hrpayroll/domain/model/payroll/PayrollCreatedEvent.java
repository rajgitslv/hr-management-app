package com.hrpayroll.domain.model.payroll;

import com.hrpayroll.domain.model.employee.EmployeeId;
import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

public class PayrollCreatedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final PayrollId payrollId;
    private final EmployeeId employeeId;
    private final YearMonth payPeriod;

    public PayrollCreatedEvent(PayrollId payrollId, EmployeeId employeeId, YearMonth payPeriod) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.payrollId = payrollId;
        this.employeeId = employeeId;
        this.payPeriod = payPeriod;
    }


    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "PayrollCreated";
    }
}
