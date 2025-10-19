package com.hrpayroll.domain.model.employee;

import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class EmployeePromotedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final EmployeeId employeeId;
    private final String newJobTitle;
    private final Money oldSalary;
    private final Money newSalary;

    public EmployeePromotedEvent(EmployeeId employeeId, String newJobTitle, Money oldSalary, Money newSalary) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.employeeId = employeeId;
        this.newJobTitle = newJobTitle;
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
        return "EmployeePromoted";
    }
}
