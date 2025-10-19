package com.hrpayroll.domain.model.employee;

import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class EmployeeStatusChangedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final EmployeeId employeeId;
    private final EmploymentStatus oldStatus;
    private final EmploymentStatus newStatus;

    public EmployeeStatusChangedEvent(EmployeeId employeeId, EmploymentStatus oldStatus, EmploymentStatus newStatus) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.employeeId = employeeId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
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
        return "EmployeeStatusChanged";
    }
}
