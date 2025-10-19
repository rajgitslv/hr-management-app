package com.hrpayroll.domain.model.payroll;

import com.hrpayroll.domain.model.employee.Money;
import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class BonusAddedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final PayrollId payrollId;
    private final Money bonusAmount;

    public BonusAddedEvent(PayrollId payrollId, Money bonusAmount) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.payrollId = payrollId;
        this.bonusAmount = bonusAmount;
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
        return "BonusAdded";
    }
}
