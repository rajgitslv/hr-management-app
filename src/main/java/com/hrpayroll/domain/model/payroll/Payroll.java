package com.hrpayroll.domain.model.payroll;

import com.hrpayroll.domain.model.employee.EmployeeId;
import com.hrpayroll.domain.model.employee.Money;
import com.hrpayroll.domain.shared.AggregateRoot;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Payroll aggregate root - represents a payroll record for an employee.
 */
public class Payroll extends AggregateRoot<PayrollId> {
    private PayrollId id;
    private EmployeeId employeeId;
    private YearMonth payPeriod;
    private Money baseSalary;
    private Money bonus;
    private Money deductions;
    private Money netPay;
    private PayrollStatus status;
    private LocalDate processedDate;
    private LocalDate paidDate;

    protected Payroll() {
    }

    private Payroll(PayrollId id, EmployeeId employeeId, YearMonth payPeriod, Money baseSalary) {
        validatePayroll(employeeId, payPeriod, baseSalary);

        this.id = id;
        this.employeeId = employeeId;
        this.payPeriod = payPeriod;
        this.baseSalary = baseSalary;
        this.bonus = Money.zero(baseSalary.getCurrency());
        this.deductions = Money.zero(baseSalary.getCurrency());
        this.netPay = baseSalary;
        this.status = PayrollStatus.PENDING;

        registerEvent(new PayrollCreatedEvent(id, employeeId, payPeriod));
    }

    public static Payroll create(EmployeeId employeeId, YearMonth payPeriod, Money baseSalary) {
        return new Payroll(PayrollId.generate(), employeeId, payPeriod, baseSalary);
    }

    // Getters
    public PayrollId getId() {
        return id;
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public YearMonth getPayPeriod() {
        return payPeriod;
    }

    public Money getBaseSalary() {
        return baseSalary;
    }

    public Money getBonus() {
        return bonus;
    }

    public Money getDeductions() {
        return deductions;
    }

    public Money getNetPay() {
        return netPay;
    }

    public PayrollStatus getStatus() {
        return status;
    }

    public LocalDate getProcessedDate() {
        return processedDate;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void addBonus(Money bonusAmount) {
        validateAmount(bonusAmount, "Bonus");
        ensureNotFinalized();

        this.bonus = this.bonus.add(bonusAmount);
        recalculateNetPay();

        registerEvent(new BonusAddedEvent(this.id, bonusAmount));
    }

    public void addDeduction(Money deductionAmount, String reason) {
        validateAmount(deductionAmount, "Deduction");
        ensureNotFinalized();

        this.deductions = this.deductions.add(deductionAmount);
        recalculateNetPay();

        registerEvent(new DeductionAddedEvent(this.id, deductionAmount, reason));
    }

    public void process() {
        if (this.status != PayrollStatus.PENDING) {
            throw new IllegalStateException("Only pending payrolls can be processed");
        }

        this.status = PayrollStatus.PROCESSED;
        this.processedDate = LocalDate.now();

        registerEvent(new PayrollProcessedEvent(this.id, this.employeeId, this.netPay));
    }

    public void markAsPaid() {
        if (this.status != PayrollStatus.PROCESSED) {
            throw new IllegalStateException("Only processed payrolls can be marked as paid");
        }

        this.status = PayrollStatus.PAID;
        this.paidDate = LocalDate.now();

        registerEvent(new PayrollPaidEvent(this.id, this.employeeId, this.netPay, this.paidDate));
    }

    public void cancel(String reason) {
        if (this.status == PayrollStatus.PAID) {
            throw new IllegalStateException("Cannot cancel paid payroll");
        }

        this.status = PayrollStatus.CANCELLED;

        registerEvent(new PayrollCancelledEvent(this.id, reason));
    }

    private void recalculateNetPay() {
        this.netPay = this.baseSalary
                .add(this.bonus)
                .subtract(this.deductions);
    }

    private void ensureNotFinalized() {
        if (this.status == PayrollStatus.PAID || this.status == PayrollStatus.CANCELLED) {
            throw new IllegalStateException("Cannot modify finalized payroll");
        }
    }

    private void validatePayroll(EmployeeId employeeId, YearMonth payPeriod, Money baseSalary) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (payPeriod == null) {
            throw new IllegalArgumentException("Pay period cannot be null");
        }
        if (baseSalary == null) {
            throw new IllegalArgumentException("Base salary cannot be null");
        }
    }

    private void validateAmount(Money amount, String fieldName) {
        if (amount == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        if (!amount.getCurrency().equals(this.baseSalary.getCurrency())) {
            throw new IllegalArgumentException(fieldName + " currency must match base salary currency");
        }
    }
}
