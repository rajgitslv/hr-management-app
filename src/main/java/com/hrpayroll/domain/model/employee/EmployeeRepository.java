package com.hrpayroll.domain.model.employee;

import com.hrpayroll.domain.model.department.DepartmentId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Employee aggregate (Domain layer).
 * Implementation will be provided in the infrastructure layer.
 */
public interface EmployeeRepository {

    Employee save(Employee employee);

    Optional<Employee> findById(EmployeeId id);

    Optional<Employee> findByEmail(Email email);

    List<Employee> findAll();

    List<Employee> findByDepartmentId(DepartmentId departmentId);

    List<Employee> findByStatus(EmploymentStatus status);

    void delete(EmployeeId id);

    boolean existsByEmail(Email email);
}
