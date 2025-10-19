package com.hrpayroll.domain.model.department;

import com.hrpayroll.domain.model.employee.Money;
import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class DepartmentBudgetUpdatedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final DepartmentId departmentId;
    private final Money oldBudget;
    private final Money newBudget;

    public DepartmentBudgetUpdatedEvent(DepartmentId departmentId, Money oldBudget, Money newBudget) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.departmentId = departmentId;
        this.oldBudget = oldBudget;
        this.newBudget = newBudget;
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
        return "DepartmentBudgetUpdated";
    }
}
