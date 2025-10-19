package com.hrpayroll.domain.model.payroll;

import com.hrpayroll.domain.model.employee.EmployeeId;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payroll aggregate (Domain layer).
 */
public interface PayrollRepository {

    Payroll save(Payroll payroll);

    Optional<Payroll> findById(PayrollId id);

    List<Payroll> findByEmployeeId(EmployeeId employeeId);

    Optional<Payroll> findByEmployeeIdAndPayPeriod(EmployeeId employeeId, YearMonth payPeriod);

    List<Payroll> findByStatus(PayrollStatus status);

    List<Payroll> findByPayPeriod(YearMonth payPeriod);

    List<Payroll> findAll();

    void delete(PayrollId id);
}
