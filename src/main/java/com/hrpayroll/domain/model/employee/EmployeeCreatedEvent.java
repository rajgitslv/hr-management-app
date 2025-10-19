package com.hrpayroll.domain.model.employee;

import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event fired when a new employee is created.
 */
public class EmployeeCreatedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final EmployeeId employeeId;
    private final Email email;
    private final String fullName;

    public EmployeeCreatedEvent(EmployeeId employeeId, Email email, String fullName) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.employeeId = employeeId;
        this.email = email;
        this.fullName = fullName;
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
        return "EmployeeCreated";
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public Email getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }
}
