package com.hrpayroll.domain.model.employee;

import com.hrpayroll.domain.model.department.DepartmentId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Employee Domain Model Tests")
class EmployeeTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final Email EMAIL = Email.of("john.doe@company.com");
    private static final String PHONE = "+1234567890";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1990, 1, 15);
    private static final LocalDate HIRE_DATE = LocalDate.of(2020, 6, 1);
    private static final DepartmentId DEPARTMENT_ID = DepartmentId.generate();
    private static final String JOB_TITLE = "Software Engineer";
    private static final Money SALARY = Money.of(75000.00, "USD");

    @Nested
    @DisplayName("Employee Creation Tests")
    class EmployeeCreationTests {

        @Test
        @DisplayName("Should create employee with valid data")
        void shouldCreateEmployeeWithValidData() {
            // When
            Employee employee = Employee.create(
                    FIRST_NAME, LAST_NAME, EMAIL, PHONE,
                    DATE_OF_BIRTH, HIRE_DATE, DEPARTMENT_ID,
                    JOB_TITLE, SALARY
            );

            // Then
            assertThat(employee).isNotNull();
            assertThat(employee.getId()).isNotNull();
            assertThat(employee.getFirstName()).isEqualTo(FIRST_NAME);
            assertThat(employee.getLastName()).isEqualTo(LAST_NAME);
            assertThat(employee.getEmail()).isEqualTo(EMAIL);
            assertThat(employee.getStatus()).isEqualTo(EmploymentStatus.ACTIVE);
            assertThat(employee.getSalary()).isEqualTo(SALARY);
        }

        @Test
        @DisplayName("Should fire EmployeeCreatedEvent when employee is created")
        void shouldFireEmployeeCreatedEvent() {
            // When
            Employee employee = Employee.create(
                    FIRST_NAME, LAST_NAME, EMAIL, PHONE,
                    DATE_OF_BIRTH, HIRE_DATE, DEPARTMENT_ID,
                    JOB_TITLE, SALARY
            );

            // Then
            assertThat(employee.getDomainEvents()).hasSize(1);
            assertThat(employee.getDomainEvents().get(0))
                    .isInstanceOf(EmployeeCreatedEvent.class);
        }

        @Test
        @DisplayName("Should throw exception when first name is null")
        void shouldThrowExceptionWhenFirstNameIsNull() {
            assertThatThrownBy(() ->
                    Employee.create(
                            null, LAST_NAME, EMAIL, PHONE,
                            DATE_OF_BIRTH, HIRE_DATE, DEPARTMENT_ID,
                            JOB_TITLE, SALARY
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("First name");
        }

        @Test
        @DisplayName("Should throw exception when last name is empty")
        void shouldThrowExceptionWhenLastNameIsEmpty() {
            assertThatThrownBy(() ->
                    Employee.create(
                            FIRST_NAME, "", EMAIL, PHONE,
                            DATE_OF_BIRTH, HIRE_DATE, DEPARTMENT_ID,
                            JOB_TITLE, SALARY
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Last name");
        }

        @Test
        @DisplayName("Should throw exception when hire date is in future")
        void shouldThrowExceptionWhenHireDateIsInFuture() {
            assertThatThrownBy(() ->
                    Employee.create(
                            FIRST_NAME, LAST_NAME, EMAIL, PHONE,
                            DATE_OF_BIRTH, LocalDate.now().plusDays(1),
                            DEPARTMENT_ID, JOB_TITLE, SALARY
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Hire date cannot be in the future");
        }

        @Test
        @DisplayName("Should throw exception when employee is under 18 at hire date")
        void shouldThrowExceptionWhenEmployeeIsUnder18() {
            LocalDate recentBirthDate = LocalDate.now().minusYears(17);
            assertThatThrownBy(() ->
                    Employee.create(
                            FIRST_NAME, LAST_NAME, EMAIL, PHONE,
                            recentBirthDate, HIRE_DATE, DEPARTMENT_ID,
                            JOB_TITLE, SALARY
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("at least 18 years old");
        }
    }

    @Nested
    @DisplayName("Employee Promotion Tests")
    class EmployeePromotionTests {

        @Test
        @DisplayName("Should promote employee successfully")
        void shouldPromoteEmployeeSuccessfully() {
            // Given
            Employee employee = createValidEmployee();
            Money newSalary = Money.of(90000.00, "USD");
            String newTitle = "Senior Software Engineer";

            // When
            employee.promote(newTitle, newSalary);

            // Then
            assertThat(employee.getJobTitle()).isEqualTo(newTitle);
            assertThat(employee.getSalary()).isEqualTo(newSalary);
            assertThat(employee.getDomainEvents()).hasSize(2);
            assertThat(employee.getDomainEvents().get(1))
                    .isInstanceOf(EmployeePromotedEvent.class);
        }

        @Test
        @DisplayName("Should throw exception when new salary is less than current")
        void shouldThrowExceptionWhenNewSalaryIsLower() {
            // Given
            Employee employee = createValidEmployee();
            Money lowerSalary = Money.of(60000.00, "USD");

            // When/Then
            assertThatThrownBy(() ->
                    employee.promote("New Title", lowerSalary)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cannot be less than current salary");
        }
    }

    @Nested
    @DisplayName("Salary Adjustment Tests")
    class SalaryAdjustmentTests {

        @Test
        @DisplayName("Should adjust salary successfully")
        void shouldAdjustSalarySuccessfully() {
            // Given
            Employee employee = createValidEmployee();
            Money newSalary = Money.of(80000.00, "USD");

            // When
            employee.adjustSalary(newSalary);

            // Then
            assertThat(employee.getSalary()).isEqualTo(newSalary);
            assertThat(employee.getDomainEvents()).hasSize(2);
        }

        @Test
        @DisplayName("Should throw exception when currency mismatch")
        void shouldThrowExceptionWhenCurrencyMismatch() {
            // Given
            Employee employee = createValidEmployee();
            Money differentCurrencySalary = Money.of(80000.00, "EUR");

            // When/Then
            assertThatThrownBy(() ->
                    employee.adjustSalary(differentCurrencySalary)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Currency must match");
        }
    }

    @Nested
    @DisplayName("Department Change Tests")
    class DepartmentChangeTests {

        @Test
        @DisplayName("Should change department successfully")
        void shouldChangeDepartmentSuccessfully() {
            // Given
            Employee employee = createValidEmployee();
            DepartmentId newDepartmentId = DepartmentId.generate();

            // When
            employee.changeDepartment(newDepartmentId);

            // Then
            assertThat(employee.getDepartmentId()).isEqualTo(newDepartmentId);
            assertThat(employee.getDomainEvents()).hasSize(2);
            assertThat(employee.getDomainEvents().get(1))
                    .isInstanceOf(EmployeeDepartmentChangedEvent.class);
        }

        @Test
        @DisplayName("Should throw exception when department ID is null")
        void shouldThrowExceptionWhenDepartmentIdIsNull() {
            // Given
            Employee employee = createValidEmployee();

            // When/Then
            assertThatThrownBy(() ->
                    employee.changeDepartment(null)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Department ID cannot be null");
        }
    }

    @Nested
    @DisplayName("Employee Termination Tests")
    class EmployeeTerminationTests {

        @Test
        @DisplayName("Should terminate employee successfully")
        void shouldTerminateEmployeeSuccessfully() {
            // Given
            Employee employee = createValidEmployee();
            String reason = "Voluntary resignation";

            // When
            employee.terminate(reason);

            // Then
            assertThat(employee.getStatus()).isEqualTo(EmploymentStatus.TERMINATED);
            assertThat(employee.getDomainEvents()).hasSize(2);
            assertThat(employee.getDomainEvents().get(1))
                    .isInstanceOf(EmployeeTerminatedEvent.class);
        }

        @Test
        @DisplayName("Should throw exception when terminating already terminated employee")
        void shouldThrowExceptionWhenTerminatingAlreadyTerminatedEmployee() {
            // Given
            Employee employee = createValidEmployee();
            employee.terminate("First termination");

            // When/Then
            assertThatThrownBy(() ->
                    employee.terminate("Second termination")
            ).isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already terminated");
        }
    }

    @Nested
    @DisplayName("Employee Status Change Tests")
    class EmployeeStatusChangeTests {

        @Test
        @DisplayName("Should suspend active employee successfully")
        void shouldSuspendActiveEmployee() {
            // Given
            Employee employee = createValidEmployee();

            // When
            employee.suspend();

            // Then
            assertThat(employee.getStatus()).isEqualTo(EmploymentStatus.SUSPENDED);
            assertThat(employee.getDomainEvents()).hasSize(2);
        }

        @Test
        @DisplayName("Should reactivate suspended employee successfully")
        void shouldReactivateSuspendedEmployee() {
            // Given
            Employee employee = createValidEmployee();
            employee.suspend();
            employee.clearDomainEvents();

            // When
            employee.reactivate();

            // Then
            assertThat(employee.getStatus()).isEqualTo(EmploymentStatus.ACTIVE);
            assertThat(employee.getDomainEvents()).hasSize(1);
        }

        @Test
        @DisplayName("Should not reactivate terminated employee")
        void shouldNotReactivateTerminatedEmployee() {
            // Given
            Employee employee = createValidEmployee();
            employee.terminate("Terminated");

            // When/Then
            assertThatThrownBy(() ->
                    employee.reactivate()
            ).isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot reactivate terminated employee");
        }
    }

    @Nested
    @DisplayName("Employee Query Methods Tests")
    class EmployeeQueryMethodsTests {

        @Test
        @DisplayName("Should return correct full name")
        void shouldReturnCorrectFullName() {
            // Given
            Employee employee = createValidEmployee();

            // When
            String fullName = employee.getFullName();

            // Then
            assertThat(fullName).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should calculate years of service correctly")
        void shouldCalculateYearsOfServiceCorrectly() {
            // Given
            LocalDate hireDate = LocalDate.now().minusYears(5);
            Employee employee = Employee.create(
                    FIRST_NAME, LAST_NAME, EMAIL, PHONE,
                    DATE_OF_BIRTH, hireDate, DEPARTMENT_ID,
                    JOB_TITLE, SALARY
            );

            // When
            int yearsOfService = employee.getYearsOfService();

            // Then
            assertThat(yearsOfService).isEqualTo(5);
        }

        @Test
        @DisplayName("Should return true when employee is active")
        void shouldReturnTrueWhenEmployeeIsActive() {
            // Given
            Employee employee = createValidEmployee();

            // When/Then
            assertThat(employee.isActive()).isTrue();
        }

        @Test
        @DisplayName("Should return false when employee is terminated")
        void shouldReturnFalseWhenEmployeeIsTerminated() {
            // Given
            Employee employee = createValidEmployee();
            employee.terminate("Terminated");

            // When/Then
            assertThat(employee.isActive()).isFalse();
        }
    }

    private Employee createValidEmployee() {
        return Employee.create(
                FIRST_NAME, LAST_NAME, EMAIL, PHONE,
                DATE_OF_BIRTH, HIRE_DATE, DEPARTMENT_ID,
                JOB_TITLE, SALARY
        );
    }
}
