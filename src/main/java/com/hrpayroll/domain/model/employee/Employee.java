package com.hrpayroll.domain.model.employee;

import com.hrpayroll.domain.model.department.DepartmentId;
import com.hrpayroll.domain.shared.AggregateRoot;

import java.time.LocalDate;

/**
 * Employee aggregate root - represents an employee in the HR system.
 * This is the main entity in the Employee bounded context.
 */
public class Employee extends AggregateRoot<EmployeeId> {
    private EmployeeId id;
    private String firstName;
    private String lastName;
    private Email email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private LocalDate hireDate;
    private DepartmentId departmentId;
    private String jobTitle;
    private Money salary;
    private EmploymentStatus status;
    private LocalDate lastModifiedDate;

    // Private constructor for JPA
    protected Employee() {
    }

    // Constructor for creating new employee
    private Employee(EmployeeId id, String firstName, String lastName, Email email,
                    String phoneNumber, LocalDate dateOfBirth, LocalDate hireDate,
                    DepartmentId departmentId, String jobTitle, Money salary) {
        validateEmployee(firstName, lastName, dateOfBirth, hireDate, jobTitle);

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.hireDate = hireDate;
        this.departmentId = departmentId;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.status = EmploymentStatus.ACTIVE;
        this.lastModifiedDate = LocalDate.now();

        // Register domain event
        registerEvent(new EmployeeCreatedEvent(id, email, firstName + " " + lastName));
    }

    public static Employee create(String firstName, String lastName, Email email,
                                 String phoneNumber, LocalDate dateOfBirth, LocalDate hireDate,
                                 DepartmentId departmentId, String jobTitle, Money salary) {
        return new Employee(EmployeeId.generate(), firstName, lastName, email,
                          phoneNumber, dateOfBirth, hireDate, departmentId, jobTitle, salary);
    }

    public static Employee reconstitute(EmployeeId id, String firstName, String lastName, Email email,
                                       String phoneNumber, LocalDate dateOfBirth, LocalDate hireDate,
                                       DepartmentId departmentId, String jobTitle, Money salary,
                                       EmploymentStatus status) {
        Employee employee = new Employee(id, firstName, lastName, email, phoneNumber,
                                        dateOfBirth, hireDate, departmentId, jobTitle, salary);
        employee.status = status;
        employee.clearDomainEvents(); // Don't fire events when reconstituting
        return employee;
    }

    // Getters
    public EmployeeId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Email getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public DepartmentId getDepartmentId() {
        return departmentId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public Money getSalary() {
        return salary;
    }

    public EmploymentStatus getStatus() {
        return status;
    }

    public LocalDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    private void setLastModifiedDate(LocalDate date) {
        this.lastModifiedDate = date;
    }

    public void updatePersonalInfo(String firstName, String lastName, String phoneNumber) {
        validateName(firstName, "First name");
        validateName(lastName, "Last name");

        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.lastModifiedDate = LocalDate.now();

        registerEvent(new EmployeeUpdatedEvent(this.id));
    }

    public void changeDepartment(DepartmentId newDepartmentId) {
        if (newDepartmentId == null) {
            throw new IllegalArgumentException("Department ID cannot be null");
        }
        if (!this.status.equals(EmploymentStatus.ACTIVE)) {
            throw new IllegalStateException("Cannot change department for non-active employee");
        }

        DepartmentId oldDepartmentId = this.departmentId;
        this.departmentId = newDepartmentId;
        this.lastModifiedDate = LocalDate.now();

        registerEvent(new EmployeeDepartmentChangedEvent(this.id, oldDepartmentId, newDepartmentId));
    }

    public void promote(String newJobTitle, Money newSalary) {
        validateJobTitle(newJobTitle);
        if (newSalary.isLessThan(this.salary)) {
            throw new IllegalArgumentException("New salary cannot be less than current salary for promotion");
        }

        Money oldSalary = this.salary;
        this.jobTitle = newJobTitle;
        this.salary = newSalary;
        this.lastModifiedDate = LocalDate.now();

        registerEvent(new EmployeePromotedEvent(this.id, newJobTitle, oldSalary, newSalary));
    }

    public void adjustSalary(Money newSalary) {
        if (newSalary.getCurrency() != this.salary.getCurrency()) {
            throw new IllegalArgumentException("Currency must match current salary currency");
        }

        Money oldSalary = this.salary;
        this.salary = newSalary;
        this.lastModifiedDate = LocalDate.now();

        registerEvent(new SalaryAdjustedEvent(this.id, oldSalary, newSalary));
    }

    public void terminate(String reason) {
        if (this.status == EmploymentStatus.TERMINATED) {
            throw new IllegalStateException("Employee is already terminated");
        }

        this.status = EmploymentStatus.TERMINATED;
        this.lastModifiedDate = LocalDate.now();

        registerEvent(new EmployeeTerminatedEvent(this.id, reason, LocalDate.now()));
    }

    public void suspend() {
        if (this.status != EmploymentStatus.ACTIVE) {
            throw new IllegalStateException("Only active employees can be suspended");
        }

        this.status = EmploymentStatus.SUSPENDED;
        this.lastModifiedDate = LocalDate.now();

        registerEvent(new EmployeeStatusChangedEvent(this.id, EmploymentStatus.ACTIVE, EmploymentStatus.SUSPENDED));
    }

    public void reactivate() {
        if (this.status == EmploymentStatus.TERMINATED) {
            throw new IllegalStateException("Cannot reactivate terminated employee");
        }

        EmploymentStatus oldStatus = this.status;
        this.status = EmploymentStatus.ACTIVE;
        this.lastModifiedDate = LocalDate.now();

        registerEvent(new EmployeeStatusChangedEvent(this.id, oldStatus, EmploymentStatus.ACTIVE));
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getYearsOfService() {
        return LocalDate.now().getYear() - hireDate.getYear();
    }

    public boolean isActive() {
        return status == EmploymentStatus.ACTIVE;
    }

    private void validateEmployee(String firstName, String lastName, LocalDate dateOfBirth,
                                  LocalDate hireDate, String jobTitle) {
        validateName(firstName, "First name");
        validateName(lastName, "Last name");
        validateDates(dateOfBirth, hireDate);
        validateJobTitle(jobTitle);
    }

    private void validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        if (name.length() < 2) {
            throw new IllegalArgumentException(fieldName + " must be at least 2 characters long");
        }
    }

    private void validateDates(LocalDate dateOfBirth, LocalDate hireDate) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        if (hireDate == null) {
            throw new IllegalArgumentException("Hire date cannot be null");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
        if (hireDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Hire date cannot be in the future");
        }
        if (dateOfBirth.plusYears(18).isAfter(hireDate)) {
            throw new IllegalArgumentException("Employee must be at least 18 years old at hire date");
        }
    }

    private void validateJobTitle(String jobTitle) {
        if (jobTitle == null || jobTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Job title cannot be null or empty");
        }
    }
}
