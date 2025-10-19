package com.hrpayroll.domain.model.payroll;

import com.hrpayroll.domain.model.employee.Money;
import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class DeductionAddedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final PayrollId payrollId;
    private final Money deductionAmount;
    private final String reason;

    public DeductionAddedEvent(PayrollId payrollId, Money deductionAmount, String reason) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.payrollId = payrollId;
        this.deductionAmount = deductionAmount;
        this.reason = reason;
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
        return "DeductionAdded";
    }
}
