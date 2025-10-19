package com.hrpayroll.domain.model.department;

import com.hrpayroll.domain.model.employee.EmployeeId;
import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class DepartmentManagerAssignedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final DepartmentId departmentId;
    private final EmployeeId managerId;

    public DepartmentManagerAssignedEvent(DepartmentId departmentId, EmployeeId managerId) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.departmentId = departmentId;
        this.managerId = managerId;
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
        return "DepartmentManagerAssigned";
    }
}
