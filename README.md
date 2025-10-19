# Employee HR and Payroll Management System

A comprehensive Spring Boot application built using **Domain-Driven Design (DDD)**, **Event-Driven Architecture (EDA)**, and **Test-Driven Development (TDD)** principles.

## Table of Contents
- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Domain Model](#domain-model)
- [Setup and Installation](#setup-and-installation)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [API Documentation](#api-documentation)
- [Monitoring and Observability](#monitoring-and-observability)
- [Deployment](#deployment)

## Architecture Overview

This application follows a **layered DDD architecture** with clear separation of concerns:

### Architectural Layers

```
┌─────────────────────────────────────────────┐
│          Presentation Layer                 │
│  (REST Controllers, DTOs, API Docs)         │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         Application Layer                   │
│  (Services, Command/Query Handlers)         │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│           Domain Layer                      │
│  (Aggregates, Entities, Value Objects,      │
│   Domain Events, Repository Interfaces)     │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│        Infrastructure Layer                 │
│  (JPA Repositories, Event Publishers,       │
│   External Services, Messaging)             │
└─────────────────────────────────────────────┘
```

### Key DDD Concepts Implemented

1. **Aggregates**: Employee, Department, Payroll
2. **Value Objects**: EmployeeId, Email, Money, DepartmentId, PayrollId
3. **Domain Events**: EmployeeCreated, PayrollProcessed, DepartmentCreated, etc.
4. **Repository Pattern**: Clean domain repository interfaces
5. **Bounded Contexts**: Employee Management, Department Management, Payroll Management

### Event-Driven Architecture

- **Domain Events**: Published when aggregate state changes
- **Kafka Integration**: Asynchronous event processing
- **JMS Integration**: Queue-based messaging for reliable processing
- **Event Handlers**: React to domain events across bounded contexts

## Technology Stack

### Core Framework
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM

### Security
- **Spring Security** - Authentication and authorization
- **OAuth 2.0** - Authorization framework
- **JWT** - Token-based authentication

### Messaging & Events
- **Apache Kafka** - Event streaming platform
- **Apache Artemis** - JMS message broker
- **Spring Kafka** - Kafka integration
- **Spring JMS** - JMS integration

### Database
- **PostgreSQL** - Production database
- **H2** - Development/testing in-memory database

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions
- **REST Assured** - API testing
- **Testcontainers** - Integration testing with containers
- **ArchUnit** - Architecture testing

### Monitoring & Observability
- **Spring Actuator** - Application monitoring
- **Micrometer** - Metrics collection
- **Prometheus** - Metrics storage
- **OpenAPI/Swagger** - API documentation

### DevOps
- **Maven** - Build tool
- **Docker** - Containerization
- **Kubernetes** - Container orchestration
- **Docker Compose** - Local development environment

## Project Structure

```
src/
├── main/
│   ├── java/com/hrpayroll/
│   │   ├── domain/                    # Domain Layer (Core Business Logic)
│   │   │   ├── shared/
│   │   │   │   ├── AggregateRoot.java
│   │   │   │   ├── DomainEvent.java
│   │   │   │   └── ValueObject.java
│   │   │   ├── model/
│   │   │   │   ├── employee/          # Employee Aggregate
│   │   │   │   │   ├── Employee.java  # Aggregate Root
│   │   │   │   │   ├── EmployeeId.java # Value Object
│   │   │   │   │   ├── Email.java     # Value Object
│   │   │   │   │   ├── Money.java     # Value Object
│   │   │   │   │   ├── EmploymentStatus.java
│   │   │   │   │   ├── EmployeeCreatedEvent.java
│   │   │   │   │   ├── EmployeePromotedEvent.java
│   │   │   │   │   ├── EmployeeTerminatedEvent.java
│   │   │   │   │   └── EmployeeRepository.java # Domain Interface
│   │   │   │   ├── department/        # Department Aggregate
│   │   │   │   │   ├── Department.java
│   │   │   │   │   ├── DepartmentId.java
│   │   │   │   │   ├── DepartmentCreatedEvent.java
│   │   │   │   │   └── DepartmentRepository.java
│   │   │   │   └── payroll/           # Payroll Aggregate
│   │   │   │       ├── Payroll.java
│   │   │   │       ├── PayrollId.java
│   │   │   │       ├── PayrollStatus.java
│   │   │   │       ├── PayrollCreatedEvent.java
│   │   │   │       ├── PayrollProcessedEvent.java
│   │   │   │       └── PayrollRepository.java
│   │   │
│   │   ├── application/               # Application Layer (Use Cases)
│   │   │   ├── service/
│   │   │   │   ├── EmployeeService.java
│   │   │   │   ├── DepartmentService.java
│   │   │   │   └── PayrollService.java
│   │   │   ├── command/              # Command handlers (CQRS)
│   │   │   │   ├── CreateEmployeeCommand.java
│   │   │   │   ├── PromoteEmployeeCommand.java
│   │   │   │   ├── ProcessPayrollCommand.java
│   │   │   │   └── ...
│   │   │   ├── query/                # Query handlers (CQRS)
│   │   │   │   ├── GetEmployeeQuery.java
│   │   │   │   ├── GetPayrollQuery.java
│   │   │   │   └── ...
│   │   │   └── dto/                  # Data Transfer Objects
│   │   │       ├── EmployeeDTO.java
│   │   │       ├── DepartmentDTO.java
│   │   │       └── PayrollDTO.java
│   │   │
│   │   ├── infrastructure/            # Infrastructure Layer
│   │   │   ├── persistence/           # JPA Implementation
│   │   │   │   ├── entity/            # JPA Entities
│   │   │   │   │   ├── EmployeeJpaEntity.java
│   │   │   │   │   ├── DepartmentJpaEntity.java
│   │   │   │   │   └── PayrollJpaEntity.java
│   │   │   │   ├── repository/        # Repository Implementations
│   │   │   │   │   ├── EmployeeRepositoryImpl.java
│   │   │   │   │   ├── DepartmentRepositoryImpl.java
│   │   │   │   │   └── PayrollRepositoryImpl.java
│   │   │   │   └── jpa/               # Spring Data JPA Repos
│   │   │   │       ├── EmployeeJpaRepository.java
│   │   │   │       ├── DepartmentJpaRepository.java
│   │   │   │       └── PayrollJpaRepository.java
│   │   │   │
│   │   │   ├── messaging/             # Event Publishing
│   │   │   │   ├── kafka/
│   │   │   │   │   ├── KafkaEventPublisher.java
│   │   │   │   │   ├── KafkaEventConsumer.java
│   │   │   │   │   └── KafkaConfig.java
│   │   │   │   └── jms/
│   │   │   │       ├── JmsMessageProducer.java
│   │   │   │       ├── JmsMessageConsumer.java
│   │   │   │       └── JmsConfig.java
│   │   │   │
│   │   │   └── security/              # Security Implementation
│   │   │       ├── JwtTokenProvider.java
│   │   │       ├── JwtAuthenticationFilter.java
│   │   │       └── SecurityConfig.java
│   │   │
│   │   ├── presentation/              # Presentation Layer
│   │   │   ├── rest/                  # REST Controllers
│   │   │   │   ├── EmployeeController.java
│   │   │   │   ├── DepartmentController.java
│   │   │   │   └── PayrollController.java
│   │   │   ├── dto/                   # API DTOs
│   │   │   │   └── request/
│   │   │   │       ├── CreateEmployeeRequest.java
│   │   │   │       └── ...
│   │   │   └── exception/             # Exception Handling
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       └── ...
│   │   │
│   │   └── EmployeeManagementApplication.java
│   │
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-prod.yml
│       └── db/migration/              # Flyway migrations (to be added)
│
└── test/
    ├── java/com/hrpayroll/
    │   ├── domain/                    # Domain Unit Tests
    │   │   ├── model/
    │   │   │   ├── employee/
    │   │   │   │   ├── EmployeeTest.java ✓
    │   │   │   │   ├── EmailTest.java
    │   │   │   │   └── MoneyTest.java
    │   │   │   ├── department/
    │   │   │   │   └── DepartmentTest.java
    │   │   │   └── payroll/
    │   │   │       └── PayrollTest.java
    │   │
    │   ├── application/               # Application Service Tests
    │   │   └── service/
    │   │       ├── EmployeeServiceTest.java
    │   │       ├── DepartmentServiceTest.java
    │   │       └── PayrollServiceTest.java
    │   │
    │   ├── infrastructure/            # Infrastructure Tests
    │   │   ├── persistence/
    │   │   │   └── EmployeeRepositoryIntegrationTest.java
    │   │   └── messaging/
    │   │       ├── KafkaIntegrationTest.java
    │   │       └── JmsIntegrationTest.java
    │   │
    │   ├── presentation/              # REST API Tests
    │   │   └── rest/
    │   │       ├── EmployeeControllerIT.java # REST Assured
    │   │       ├── DepartmentControllerIT.java
    │   │       └── PayrollControllerIT.java
    │   │
    │   └── architecture/              # Architecture Tests
    │       └── ArchitectureTest.java # ArchUnit tests
    │
    └── resources/
        └── application-test.yml
```

## Domain Model

### Employee Aggregate
- **Aggregate Root**: Employee
- **Value Objects**: EmployeeId, Email, Money
- **Operations**:
  - Create employee
  - Update personal information
  - Promote employee
  - Adjust salary
  - Change department
  - Terminate employment
  - Suspend/Reactivate
- **Domain Events**:
  - EmployeeCreatedEvent
  - EmployeePromotedEvent
  - SalaryAdjustedEvent
  - EmployeeTerminatedEvent
  - EmployeeDepartmentChangedEvent

### Department Aggregate
- **Aggregate Root**: Department
- **Value Objects**: DepartmentId
- **Operations**:
  - Create department
  - Assign manager
  - Update budget
  - Add/Remove employees
- **Domain Events**:
  - DepartmentCreatedEvent
  - DepartmentManagerAssignedEvent
  - DepartmentBudgetUpdatedEvent

### Payroll Aggregate
- **Aggregate Root**: Payroll
- **Value Objects**: PayrollId
- **Operations**:
  - Create payroll
  - Add bonus
  - Add deduction
  - Process payroll
  - Mark as paid
  - Cancel payroll
- **Domain Events**:
  - PayrollCreatedEvent
  - BonusAddedEvent
  - DeductionAddedEvent
  - PayrollProcessedEvent
  - PayrollPaidEvent

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- Docker and Docker Compose
- PostgreSQL 15 (for production)
- Kafka (optional, included in docker-compose)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd employee-management-ddd
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run with H2 in-memory database** (Dev profile)
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. **Run with Docker Compose** (Full stack)
   ```bash
   docker-compose up -d
   ```

## Running the Application

### Development Mode
```bash
# With H2 in-memory database
mvn spring-boot:run

# With specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production Mode
```bash
# Ensure PostgreSQL, Kafka, and Artemis are running
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Docker
```bash
# Build image
docker build -t hrpayroll/employee-management-ddd:latest .

# Run with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f app
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Unit Tests Only
```bash
mvn test -Dtest="*Test"
```

### Run Integration Tests Only
```bash
mvn verify -Dtest="*IT"
```

### Run REST Assured API Tests
```bash
mvn verify -Dtest="*ControllerIT"
```

### Test Coverage
```bash
mvn verify jacoco:report
# Report available at: target/site/jacoco/index.html
```

## API Documentation

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON
```
http://localhost:8080/api-docs
```

### Key Endpoints

#### Employee Management
- `POST /api/v1/employees` - Create employee
- `GET /api/v1/employees/{id}` - Get employee by ID
- `GET /api/v1/employees` - List all employees
- `PUT /api/v1/employees/{id}` - Update employee
- `POST /api/v1/employees/{id}/promote` - Promote employee
- `POST /api/v1/employees/{id}/terminate` - Terminate employee
- `DELETE /api/v1/employees/{id}` - Delete employee

#### Department Management
- `POST /api/v1/departments` - Create department
- `GET /api/v1/departments/{id}` - Get department by ID
- `GET /api/v1/departments` - List all departments
- `PUT /api/v1/departments/{id}` - Update department
- `POST /api/v1/departments/{id}/assign-manager` - Assign manager

#### Payroll Management
- `POST /api/v1/payrolls` - Create payroll
- `GET /api/v1/payrolls/{id}` - Get payroll by ID
- `GET /api/v1/payrolls/employee/{employeeId}` - Get employee payrolls
- `POST /api/v1/payrolls/{id}/process` - Process payroll
- `POST /api/v1/payrolls/{id}/pay` - Mark payroll as paid

## Monitoring and Observability

### Spring Actuator Endpoints
```
http://localhost:8080/actuator
```

Available endpoints:
- `/actuator/health` - Application health
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics
- `/actuator/env` - Environment properties

### Prometheus Metrics
Metrics are exposed at:
```
http://localhost:8080/actuator/prometheus
```

### Health Checks
```bash
# Overall health
curl http://localhost:8080/actuator/health

# Liveness probe
curl http://localhost:8080/actuator/health/liveness

# Readiness probe
curl http://localhost:8080/actuator/health/readiness
```

## Deployment

### Docker Deployment
```bash
# Build image
docker build -t hrpayroll/employee-management-ddd:latest .

# Push to registry
docker push hrpayroll/employee-management-ddd:latest

# Run
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=postgres \
  -e KAFKA_BROKERS=kafka:9092 \
  hrpayroll/employee-management-ddd:latest
```

### Kubernetes Deployment
```bash
# Create namespace
kubectl apply -f k8s/namespace.yml

# Apply configurations
kubectl apply -f k8s/configmap.yml
kubectl apply -f k8s/secret.yml

# Deploy application
kubectl apply -f k8s/deployment.yml

# Check status
kubectl get pods -n hrpayroll
kubectl get services -n hrpayroll

# View logs
kubectl logs -f deployment/hrpayroll-app -n hrpayroll
```

## Architecture Principles

### Domain-Driven Design (DDD)
1. **Ubiquitous Language**: Consistent terminology across code and business
2. **Bounded Contexts**: Clear boundaries between aggregates
3. **Aggregate Roots**: Employee, Department, Payroll as consistency boundaries
4. **Value Objects**: Immutable objects representing domain concepts
5. **Domain Events**: Capture significant business events

### Event-Driven Architecture (EDA)
1. **Event Publishing**: Domain events published on state changes
2. **Event Handlers**: React to events asynchronously
3. **Event Sourcing Ready**: Events capture complete history
4. **Eventual Consistency**: Across bounded contexts

### Test-Driven Development (TDD)
1. **Unit Tests**: Domain logic tested in isolation
2. **Integration Tests**: Infrastructure and persistence tested
3. **API Tests**: REST endpoints tested with REST Assured
4. **Architecture Tests**: Enforce layering with ArchUnit

## Additional Components to Implement

The following components have been scaffolded but require implementation:

### Application Layer
- [ ] Command handlers for CQRS pattern
- [ ] Query handlers for read operations
- [ ] Application services coordinating use cases
- [ ] DTO mappers (MapStruct configurations)

### Infrastructure Layer
- [ ] JPA entity implementations
- [ ] Repository adapter implementations
- [ ] Kafka event publisher and consumer
- [ ] JMS message producer and consumer
- [ ] Event handler configurations

### Presentation Layer
- [ ] REST controllers for all aggregates
- [ ] Request/Response DTOs
- [ ] Global exception handler
- [ ] API versioning strategy

### Security
- [ ] JWT token provider implementation
- [ ] Authentication filter
- [ ] OAuth 2.0 configuration
- [ ] Role-based access control

### Testing
- [ ] Complete domain model tests
- [ ] Application service tests
- [ ] Integration tests with Testcontainers
- [ ] REST Assured API tests
- [ ] ArchUnit architecture tests

## Contributing
Follow DDD, TDD, and clean code principles. All pull requests require:
- Unit tests with >80% coverage
- Integration tests for infrastructure
- API tests for new endpoints
- Architecture conformance

## License
[Your License Here]

---

**Built with Domain-Driven Design, Event-Driven Architecture, and Test-Driven Development**
