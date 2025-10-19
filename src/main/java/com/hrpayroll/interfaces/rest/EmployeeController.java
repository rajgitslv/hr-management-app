package com.hrpayroll.interfaces.rest;

import com.hrpayroll.domain.model.employee.*;
import com.hrpayroll.interfaces.rest.dto.CreateEmployeeRequest;
import com.hrpayroll.interfaces.rest.dto.EmployeeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * REST API Controller for Employee operations.
 */
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    // In-memory storage for demo purposes
    private final Map<String, Employee> employeeStore = new HashMap<>();

    @PostMapping
    @Operation(summary = "Create a new employee", description = "Creates a new employee in the system")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        try {
            // Create value objects using factory methods
            Email email = Email.of(request.getEmail());
            Money salary = Money.of(
                request.getSalary(),
                Currency.getInstance(request.getCurrency() != null ? request.getCurrency() : "USD")
            );
            LocalDate dateOfBirth = LocalDate.parse(request.getDateOfBirth());

            // Create employee aggregate using factory method
            Employee employee = Employee.create(
                request.getFirstName(),
                request.getLastName(),
                email,
                null, // phoneNumber
                dateOfBirth,
                LocalDate.now(), // hireDate
                null, // departmentId
                request.getJobTitle(),
                salary
            );

            // Store in memory using employee ID
            employeeStore.put(employee.getId().getValue().toString(), employee);

            // Convert to response
            EmployeeResponse response = toResponse(employee);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = Map.of("error", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Retrieves an employee by their ID")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable String id) {
        Employee employee = employeeStore.get(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(employee));
    }

    @GetMapping
    @Operation(summary = "Get all employees", description = "Retrieves all employees in the system")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> employees = employeeStore.values().stream()
            .map(this::toResponse)
            .toList();
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}/promote")
    @Operation(summary = "Promote employee", description = "Promotes an employee to a new job title with new salary")
    public ResponseEntity<EmployeeResponse> promoteEmployee(
        @PathVariable String id,
        @RequestParam String newJobTitle,
        @RequestParam BigDecimal newSalary,
        @RequestParam(required = false) String currency
    ) {
        Employee employee = employeeStore.get(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Money newSalaryMoney = Money.of(
                newSalary,
                Currency.getInstance(currency != null ? currency : "USD")
            );
            employee.promote(newJobTitle, newSalaryMoney);
            return ResponseEntity.ok(toResponse(employee));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/salary")
    @Operation(summary = "Adjust employee salary", description = "Adjusts an employee's salary")
    public ResponseEntity<EmployeeResponse> adjustSalary(
        @PathVariable String id,
        @RequestParam BigDecimal newSalary,
        @RequestParam(required = false) String currency
    ) {
        Employee employee = employeeStore.get(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Money newSalaryMoney = Money.of(
                newSalary,
                Currency.getInstance(currency != null ? currency : "USD")
            );
            employee.adjustSalary(newSalaryMoney);
            return ResponseEntity.ok(toResponse(employee));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/reactivate")
    @Operation(summary = "Reactivate employee", description = "Reactivates a suspended employee")
    public ResponseEntity<EmployeeResponse> reactivateEmployee(@PathVariable String id) {
        Employee employee = employeeStore.get(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            employee.reactivate();
            return ResponseEntity.ok(toResponse(employee));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/suspend")
    @Operation(summary = "Suspend employee", description = "Suspends an employee")
    public ResponseEntity<EmployeeResponse> suspendEmployee(@PathVariable String id) {
        Employee employee = employeeStore.get(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            employee.suspend();
            return ResponseEntity.ok(toResponse(employee));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Terminate employee", description = "Terminates an employee")
    public ResponseEntity<Void> terminateEmployee(
        @PathVariable String id,
        @RequestParam(required = false, defaultValue = "Standard termination") String reason
    ) {
        Employee employee = employeeStore.get(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            employee.terminate(reason);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
            employee.getId().getValue().toString(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.getEmail().getValue(),
            employee.getJobTitle(),
            employee.getSalary().getAmount(),
            employee.getSalary().getCurrency().getCurrencyCode(),
            employee.getStatus().toString()
        );
    }
}
