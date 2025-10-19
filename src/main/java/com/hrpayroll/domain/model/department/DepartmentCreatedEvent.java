package com.hrpayroll.domain.model.department;

import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class DepartmentCreatedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final DepartmentId departmentId;
    private final String departmentName;

    public DepartmentCreatedEvent(DepartmentId departmentId, String departmentName) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.departmentId = departmentId;
        this.departmentName = departmentName;
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
        return "DepartmentCreated";
    }
}
