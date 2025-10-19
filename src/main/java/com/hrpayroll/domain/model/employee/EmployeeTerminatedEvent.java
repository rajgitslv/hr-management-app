package com.hrpayroll.domain.model.employee;

import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class EmployeeTerminatedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final EmployeeId employeeId;
    private final String reason;
    private final LocalDate terminationDate;

    public EmployeeTerminatedEvent(EmployeeId employeeId, String reason, LocalDate terminationDate) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.employeeId = employeeId;
        this.reason = reason;
        this.terminationDate = terminationDate;
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
        return "EmployeeTerminated";
    }
}
