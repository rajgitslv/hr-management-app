package com.hrpayroll.domain.model.payroll;

import com.hrpayroll.domain.model.employee.EmployeeId;
import com.hrpayroll.domain.model.employee.Money;
import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class PayrollProcessedEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final PayrollId payrollId;
    private final EmployeeId employeeId;
    private final Money netPay;

    public PayrollProcessedEvent(PayrollId payrollId, EmployeeId employeeId, Money netPay) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.payrollId = payrollId;
        this.employeeId = employeeId;
        this.netPay = netPay;
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
        return "PayrollProcessed";
    }
}
