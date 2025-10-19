package com.hrpayroll.domain.model.payroll;

import com.hrpayroll.domain.model.employee.EmployeeId;
import com.hrpayroll.domain.model.employee.Money;
import com.hrpayroll.domain.shared.DomainEvent;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class PayrollPaidEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final PayrollId payrollId;
    private final EmployeeId employeeId;
    private final Money amount;
    private final LocalDate paidDate;

    public PayrollPaidEvent(PayrollId payrollId, EmployeeId employeeId, Money amount, LocalDate paidDate) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.payrollId = payrollId;
        this.employeeId = employeeId;
        this.amount = amount;
        this.paidDate = paidDate;
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
        return "PayrollPaid";
    }
}
