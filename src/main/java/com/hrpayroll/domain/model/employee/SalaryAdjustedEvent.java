package com.hrpayroll.domain.model.employee;

import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class SalaryAdjustedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final EmployeeId employeeId;
    private final Money oldSalary;
    private final Money newSalary;

    public SalaryAdjustedEvent(EmployeeId employeeId, Money oldSalary, Money newSalary) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.employeeId = employeeId;
        this.oldSalary = oldSalary;
        this.newSalary = newSalary;
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
        return "SalaryAdjusted";
    }
}
