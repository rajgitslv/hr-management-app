package com.hrpayroll.domain.model.employee;

import com.hrpayroll.domain.model.department.DepartmentId;
import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class EmployeeDepartmentChangedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final EmployeeId employeeId;
    private final DepartmentId oldDepartmentId;
    private final DepartmentId newDepartmentId;

    public EmployeeDepartmentChangedEvent(EmployeeId employeeId, DepartmentId oldDepartmentId, DepartmentId newDepartmentId) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.employeeId = employeeId;
        this.oldDepartmentId = oldDepartmentId;
        this.newDepartmentId = newDepartmentId;
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
        return "EmployeeDepartmentChanged";
    }
}
