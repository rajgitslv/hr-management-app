# Implementation Guide

This guide provides step-by-step instructions for implementing the remaining components of the Employee HR and Payroll Management System.

## Table of Contents
1. [Infrastructure Layer](#infrastructure-layer)
2. [Application Layer](#application-layer)
3. [Presentation Layer](#presentation-layer)
4. [Security Layer](#security-layer)
5. [Testing](#testing)
6. [Best Practices](#best-practices)

## Infrastructure Layer

### 1. JPA Entity Implementations

Create JPA entities that map domain models to database tables.

#### Example: EmployeeJpaEntity

```java
package com.hrpayroll.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Data
public class EmployeeJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Column(nullable = false)
    private UUID departmentId;

    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private BigDecimal salary;

    @Column(nullable = false)
    private String salaryCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String status;

    private LocalDate lastModifiedDate;

    @Version
    private Long version; // Optimistic locking
}
```

### 2. Repository Implementations

Create adapter implementations that bridge domain repositories and JPA repositories.

#### Example: EmployeeRepositoryImpl

```java
package com.hrpayroll.infrastructure.persistence.repository;

import com.hrpayroll.domain.model.employee.*;
import com.hrpayroll.infrastructure.persistence.entity.EmployeeJpaEntity;
import com.hrpayroll.infrastructure.persistence.jpa.EmployeeJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {
    private final EmployeeJpaRepository jpaRepository;
    private final EmployeeMapper mapper;

    // Implementation methods...

    @Override
    public Employee save(Employee employee) {
        EmployeeJpaEntity entity = mapper.toJpaEntity(employee);
        EmployeeJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

### 3. Spring Data JPA Repository

```java
package com.hrpayroll.infrastructure.persistence.jpa;

import com.hrpayroll.infrastructure.persistence.entity.EmployeeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeJpaEntity, UUID> {
    Optional<EmployeeJpaEntity> findByEmail(String email);
    List<EmployeeJpaEntity> findByDepartmentId(UUID departmentId);
    List<EmployeeJpaEntity> findByStatus(String status);
}
```

### 4. Kafka Event Publisher

```java
package com.hrpayroll.infrastructure.messaging.kafka;

import com.hrpayroll.domain.shared.DomainEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(DomainEvent event) {
        String topic = "hr-payroll." + event.getEventType();
        kafkaTemplate.send(topic, event.getEventId().toString(), event);
    }
}
```

### 5. Kafka Event Consumer

```java
package com.hrpayroll.infrastructure.messaging.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventConsumer {

    @KafkaListener(topics = "hr-payroll.EmployeeCreated", groupId = "hr-payroll-group")
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        // Process event
        log.info("Received EmployeeCreated event: {}", event);
    }

    @KafkaListener(topics = "hr-payroll.PayrollProcessed", groupId = "hr-payroll-group")
    public void handlePayrollProcessed(PayrollProcessedEvent event) {
        // Process event - e.g., send notification
        log.info("Received PayrollProcessed event: {}", event);
    }
}
```

### 6. JMS Message Producer

```java
package com.hrpayroll.infrastructure.messaging.jms;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class JmsMessageProducer {
    private final JmsTemplate jmsTemplate;

    public void sendMessage(String queueName, Object message) {
        jmsTemplate.convertAndSend(queueName, message);
    }
}
```

### 7. JMS Message Consumer

```java
package com.hrpayroll.infrastructure.messaging.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class JmsMessageConsumer {

    @JmsListener(destination = "hr.payroll.notifications")
    public void receiveNotification(String message) {
        log.info("Received JMS message: {}", message);
    }
}
```

## Application Layer

### 1. Application Service

```java
package com.hrpayroll.application.service;

import com.hrpayroll.domain.model.employee.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DomainEventPublisher eventPublisher;

    public EmployeeDTO createEmployee(CreateEmployeeCommand command) {
        // 1. Validate command
        // 2. Create domain object
        Employee employee = Employee.create(
            command.getFirstName(),
            command.getLastName(),
            Email.of(command.getEmail()),
            // ... other fields
        );

        // 3. Save to repository
        Employee saved = employeeRepository.save(employee);

        // 4. Publish events
        saved.getDomainEvents().forEach(eventPublisher::publish);
        saved.clearDomainEvents();

        // 5. Return DTO
        return EmployeeMapper.toDTO(saved);
    }
}
```

### 2. Command Objects

```java
package com.hrpayroll.application.command;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateEmployeeCommand {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private LocalDate hireDate;

    @NotNull
    private UUID departmentId;

    @NotBlank
    private String jobTitle;

    @NotNull
    @Positive
    private BigDecimal salary;

    @NotBlank
    private String currency;
}
```

### 3. Query Objects

```java
package com.hrpayroll.application.query;

import lombok.Data;

@Data
public class GetEmployeeQuery {
    private UUID employeeId;
}

@Data
public class SearchEmployeesQuery {
    private String firstName;
    private String lastName;
    private UUID departmentId;
    private EmploymentStatus status;
    private int page = 0;
    private int size = 20;
}
```

### 4. DTOs

```java
package com.hrpayroll.application.dto;

import lombok.Data;

@Data
public class EmployeeDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private LocalDate hireDate;
    private String departmentId;
    private String jobTitle;
    private MoneyDTO salary;
    private String status;
}

@Data
public class MoneyDTO {
    private BigDecimal amount;
    private String currency;
}
```

## Presentation Layer

### 1. REST Controller

```java
package com.hrpayroll.presentation.rest;

import com.hrpayroll.application.service.EmployeeService;
import com.hrpayroll.application.command.CreateEmployeeCommand;
import com.hrpayroll.application.dto.EmployeeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Management", description = "Employee CRUD operations")
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('HR_MANAGER')")
    @Operation(summary = "Create a new employee")
    public ResponseEntity<EmployeeDTO> createEmployee(
            @Valid @RequestBody CreateEmployeeCommand command) {
        EmployeeDTO employee = employeeService.createEmployee(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable UUID id) {
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping
    @Operation(summary = "List all employees")
    public ResponseEntity<Page<EmployeeDTO>> listEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<EmployeeDTO> employees = employeeService.listEmployees(page, size);
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR_MANAGER')")
    @Operation(summary = "Update employee")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeCommand command) {
        EmployeeDTO employee = employeeService.updateEmployee(id, command);
        return ResponseEntity.ok(employee);
    }

    @PostMapping("/{id}/promote")
    @PreAuthorize("hasRole('HR_MANAGER')")
    @Operation(summary = "Promote employee")
    public ResponseEntity<EmployeeDTO> promoteEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody PromoteEmployeeCommand command) {
        EmployeeDTO employee = employeeService.promoteEmployee(id, command);
        return ResponseEntity.ok(employee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    @Operation(summary = "Delete employee")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 2. Global Exception Handler

```java
package com.hrpayroll.presentation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            Instant.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

## Security Layer

### 1. Security Configuration

```java
package com.hrpayroll.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**", "/swagger-ui/**", "/api-docs/**").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter()))
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
```

### 2. JWT Token Provider

```java
package com.hrpayroll.infrastructure.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

## Testing

### 1. Domain Model Tests (Already Created)

See `EmployeeTest.java` for reference.

### 2. Application Service Test

```java
package com.hrpayroll.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void shouldCreateEmployeeSuccessfully() {
        // Given
        CreateEmployeeCommand command = createValidCommand();
        Employee employee = createValidEmployee();
        when(employeeRepository.save(any())).thenReturn(employee);

        // When
        EmployeeDTO result = employeeService.createEmployee(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(command.getFirstName());
        verify(employeeRepository).save(any());
        verify(eventPublisher).publish(any());
    }
}
```

### 3. Integration Test with REST Assured

```java
package com.hrpayroll.presentation.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class EmployeeControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";
    }

    @Test
    void shouldCreateEmployeeSuccessfully() {
        given()
            .contentType(ContentType.JSON)
            .body(createEmployeeRequest())
        .when()
            .post("/employees")
        .then()
            .statusCode(201)
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Doe"))
            .body("id", notNullValue());
    }

    @Test
    void shouldGetEmployeeById() {
        String id = createEmployee();

        given()
            .pathParam("id", id)
        .when()
            .get("/employees/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(id))
            .body("firstName", notNullValue());
    }
}
```

### 4. Architecture Test with ArchUnit

```java
package com.hrpayroll.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.hrpayroll")
class ArchitectureTest {

    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
        .layer("Presentation").definedBy("..presentation..")
        .layer("Application").definedBy("..application..")
        .layer("Domain").definedBy("..domain..")
        .layer("Infrastructure").definedBy("..infrastructure..")

        .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
        .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation")
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
        .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Application");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_infrastructure =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..");
}
```

## Best Practices

### 1. Always Use Value Objects
- Encapsulate primitive types
- Add validation in constructors
- Make them immutable

### 2. Keep Aggregates Small
- Only include entities that need to change together
- Use references (IDs) to other aggregates

### 3. Publish Domain Events
- Events are past tense (EmployeeCreated, not CreateEmployee)
- Include all necessary data in events
- Clear events after publishing

### 4. Use DTOs at Boundaries
- Never expose domain objects through APIs
- Map between domain and DTOs in application layer

### 5. Write Tests First (TDD)
- Write test for behavior
- Implement minimum code to pass
- Refactor

### 6. Follow SOLID Principles
- Single Responsibility
- Open/Closed
- Liskov Substitution
- Interface Segregation
- Dependency Inversion

---

**Continue implementing following this guide to complete the application.**
