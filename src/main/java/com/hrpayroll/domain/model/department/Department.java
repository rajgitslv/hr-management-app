package com.hrpayroll.domain.model.department;

import com.hrpayroll.domain.model.employee.EmployeeId;
import com.hrpayroll.domain.model.employee.Money;
import com.hrpayroll.domain.shared.AggregateRoot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Department aggregate root.
 */
public class Department extends AggregateRoot<DepartmentId> {
    private DepartmentId id;
    private String name;
    private String description;
    private EmployeeId managerId;
    private Money budget;
    private LocalDate createdDate;
    private List<EmployeeId> employeeIds;

    protected Department() {
        this.employeeIds = new ArrayList<>();
    }

    private Department(DepartmentId id, String name, String description, Money budget) {
        validateDepartment(name, budget);

        this.id = id;
        this.name = name;
        this.description = description;
        this.budget = budget;
        this.createdDate = LocalDate.now();
        this.employeeIds = new ArrayList<>();

        registerEvent(new DepartmentCreatedEvent(id, name));
    }

    public static Department create(String name, String description, Money budget) {
        return new Department(DepartmentId.generate(), name, description, budget);
    }

    // Getters
    public DepartmentId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public EmployeeId getManagerId() {
        return managerId;
    }

    public Money getBudget() {
        return budget;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void assignManager(EmployeeId managerId) {
        if (managerId == null) {
            throw new IllegalArgumentException("Manager ID cannot be null");
        }
        this.managerId = managerId;
        registerEvent(new DepartmentManagerAssignedEvent(this.id, managerId));
    }

    public void updateBudget(Money newBudget) {
        if (newBudget == null) {
            throw new IllegalArgumentException("Budget cannot be null");
        }
        Money oldBudget = this.budget;
        this.budget = newBudget;
        registerEvent(new DepartmentBudgetUpdatedEvent(this.id, oldBudget, newBudget));
    }

    public void addEmployee(EmployeeId employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (!employeeIds.contains(employeeId)) {
            employeeIds.add(employeeId);
        }
    }

    public void removeEmployee(EmployeeId employeeId) {
        employeeIds.remove(employeeId);
    }

    public List<EmployeeId> getEmployeeIds() {
        return Collections.unmodifiableList(employeeIds);
    }

    public int getEmployeeCount() {
        return employeeIds.size();
    }

    private void validateDepartment(String name, Money budget) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        if (budget == null) {
            throw new IllegalArgumentException("Budget cannot be null");
        }
    }
}
