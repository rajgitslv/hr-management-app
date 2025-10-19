package com.hrpayroll.domain.model.employee;

import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class EmployeeUpdatedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final EmployeeId employeeId;

    public EmployeeUpdatedEvent(EmployeeId employeeId) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.employeeId = employeeId;
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
        return "EmployeeUpdated";
    }
}
