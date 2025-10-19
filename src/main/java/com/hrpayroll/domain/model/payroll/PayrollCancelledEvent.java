package com.hrpayroll.domain.model.payroll;

import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class PayrollCancelledEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final PayrollId payrollId;
    private final String reason;

    public PayrollCancelledEvent(PayrollId payrollId, String reason) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.payrollId = payrollId;
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
        return "PayrollCancelled";
    }
}
