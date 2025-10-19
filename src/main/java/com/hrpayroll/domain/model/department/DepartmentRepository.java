package com.hrpayroll.domain.model.department;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Department aggregate (Domain layer).
 */
public interface DepartmentRepository {

    Department save(Department department);

    Optional<Department> findById(DepartmentId id);

    Optional<Department> findByName(String name);

    List<Department> findAll();

    void delete(DepartmentId id);

    boolean existsByName(String name);
}
