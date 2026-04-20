# 🚀 API Automation Framework – Rest Assured (Java)

### ✍️ Author: Pramod Dutta

A robust **API Automation Framework** built using **Rest Assured** for testing the CRUD operations of the **Restful Booker API**. This framework follows a **hybrid design pattern**, integrates CI/CD pipelines, and generates rich **Allure Reports**.

---

## 📌 Table of Contents

* [Overview](#-overview)
* [Tech Stack](#-tech-stack)
* [Architecture](#-architecture)
* [Framework Flow](#-framework-flow)
* [Project Structure](#-project-structure)
* [Setup & Execution](#-setup--execution)
* [E2E Integration Test Flows](#-e2e-integration-test-flows)
* [Retry Analyzer & Listeners](#-retry-analyzer--listeners)
* [Log4j Logging](#-log4j-logging)
* [MySQL DB Integration](#-mysql-db-integration)
* [Parallel Execution](#-parallel-execution)
* [Allure Reporting](#-allure-reporting)
* [CI/CD Integration](#-cicd-integration)
* [JSON Schema Validation](#-json-schema-validation)
* [Best Practices](#-best-practices)

---

## 📖 Overview

This framework is designed to:

* Automate **CRUD operations** of REST APIs
* Provide **scalable and maintainable test architecture**
* Support **parallel execution**
* Integrate with **CI/CD pipelines**
* Generate **detailed reports with Allure**
* Validate API responses against **MySQL Database**
* Provide comprehensive **Log4j logging**

---

## 🛠 Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java (JDK 23+) |
| API Testing | Rest Assured 6.0.0, JSON Schema Validator 5.4.0 |
| Test Framework | TestNG 7.12.0 |
| Build Tool | Maven |
| Assertions | AssertJ 3.27.7 |
| JSON Parsing | GSON 2.13.2 |
| Logging | Log4j2 2.23.1 |
| Reporting | Allure TestNG 2.33.0 |
| Test Data | Apache POI 5.3.0, JavaFaker 1.0.2 |
| Database | MySQL Connector/J 9.3.0 |
| Config | dotenv-java 3.0.0, SnakeYAML 2.2 |
| CI/CD | Jenkins |

---

## 🏗 Architecture

The framework follows a **Hybrid Framework Design** combining:

* API abstraction layer
* Data-driven testing with POJOs
* Utility-based reusable components
* Log4j logging throughout

```mermaid
flowchart TD
    subgraph TestLayer["Test Layer"]
        A["TestNG Test Classes"]
        A1["TestIntegrationFlow1"]
        A2["TestIntegrationFlow2"]
        A3["TestIntegrationFlow3"]
        A4["TestIntegrationFlow4"]
    end

    subgraph CoreLayer["Core Layer"]
        B["BaseTest"]
        C["PayloadManager"]
        D["AssertActions"]
    end

    subgraph DataLayer["Data Layer"]
        E["POJOs"]
        F["APIConstants"]
    end

    subgraph UtilLayer["Utility Layer"]
        G["MySqlDBConnector"]
        H["EnvUtil"]
        I["Log4j Logger"]
    end

    subgraph External["External Systems"]
        J["REST API Server"]
        K["MySQL Database"]
        L["Allure Reports"]
    end

    A --> A1 & A2 & A3 & A4
    A1 & A2 & A3 & A4 --> B
    B --> C & D
    C --> E
    B --> F
    B --> G & H & I
    C --> J
    G --> K
    D --> L
```

---

## 🔄 Framework Flow

```mermaid
sequenceDiagram
    participant Test as TestNG Test
    participant Base as BaseTest
    participant PM as PayloadManager
    participant RA as Rest Assured
    participant Server as API Server
    participant DB as MySQL DB
    participant Log as Log4j
    participant Report as Allure

    Test->>Log: Log test start
    Test->>Base: Setup (BeforeClass)
    Base->>PM: Initialize PayloadManager
    Test->>PM: Create Request Payload
    PM->>RA: Build Request
    RA->>Server: HTTP Request (POST/GET/PUT/PATCH/DELETE)
    Server-->>RA: HTTP Response
    RA-->>Test: Parsed Response
    Test->>DB: Validate in Database (Flow4)
    DB-->>Test: DB Records
    Test->>Log: Log assertions
    Test->>Report: Record Results
```

---

## 📁 Project Structure

```mermaid
graph TD
    subgraph src/main/java
        A[com.thetestingacademy]
        A --> B[endpoints]
        A --> C[modules]
        A --> D[pojos]
        A --> E[utils]

        B --> B1[APIConstants.java]
        C --> C1[PayloadManager.java]
        C --> C2[vwo/VWOPayloadManager.java]
        D --> D1[requestPOJOs]
        D --> D2[responsePojos]
        D1 --> D1a[Booking.java]
        D1 --> D1b[PartialBookingUpdate.java]
        D1 --> D1c[Auth.java]
        E --> E1[MySqlDBConnector.java]
        E --> E2[EnvUtil.java]
    end

    subgraph src/test/java
        F[com.thetestingacademy]
        F --> G[base]
        F --> H[tests]
        F --> I[asserts]
        F --> L[listeners]

        G --> G1[BaseTest.java]
        H --> H1[e2e_integration]
        H1 --> H1a[TestIntegrationFlow1.java]
        H1 --> H1b[TestIntegrationFlow2.java]
        H1 --> H1c[TestIntegrationFlow3.java]
        H1 --> H1d[TestIntegrationFlow4.java]
        I --> I1[AssertActions.java]
        L --> L1[RetryAnalyzer.java]
        L --> L2[RetryListener.java]
        L --> L3[TestListener.java]
    end

    subgraph src/test/resources
        J[Resources]
        J --> J1[log4j2.xml]
        J --> J2[config.yaml]
        J --> J3[data.properties]
    end
```

---

## ⚙️ Setup & Execution

### 🔹 Prerequisites

* Install Java (JDK 23+)
* Install Maven
* (Optional) Install MySQL for DB validation tests

### 🔹 Environment Setup

1. Copy the sample environment file:
```bash
cp .env.sample .env
```

2. Edit `.env` and fill in your credentials:
```env
# API Credentials
USERNAME=your_username
PASSWORD=your_password

# MySQL (for Flow4)
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=api_automation
MYSQL_USERNAME=your_db_username
MYSQL_PASSWORD=your_db_password
```

**Important:** The `.env` file is in `.gitignore` and will NOT be committed to version control.

### 🔹 Run Commands

```bash
# Run all tests with default suite
mvn test

# Run E2E integration tests
mvn test -Dsurefire.suiteXmlFiles=testng-e2e.xml

# Run sample tests
mvn test -Dsurefire.suiteXmlFiles=testng-sample.xml

# Run a specific test class
mvn test -Dtest=com.thetestingacademy.tests.e2e_integration.TestIntegrationFlow1

# Compile without running tests
mvn clean compile
```

---

## 🔗 E2E Integration Test Flows

The framework includes **4 comprehensive E2E integration test flows**:

```mermaid
flowchart LR
    subgraph Flow1["Flow 1: Full CRUD"]
        F1A[Create Booking] --> F1B[Verify Booking]
        F1B --> F1C[Update - PUT]
        F1C --> F1D[Delete Booking]
    end

    subgraph Flow2["Flow 2: Partial Update"]
        F2A[Create Booking] --> F2B[Verify Booking]
        F2B --> F2C[Update - PATCH]
        F2C --> F2D[Verify PATCH]
    end

    subgraph Flow3["Flow 3: Create & Delete"]
        F3A[Create Booking] --> F3B[Delete Immediately]
        F3B --> F3C[Verify 404]
    end

    subgraph Flow4["Flow 4: DB Validation"]
        F4A[Create Booking] --> F4B[Validate in DB]
        F4B --> F4C[Update Booking]
        F4C --> F4D[Verify Update in DB]
    end
```

### Test Flow Details

| Flow | Class | Steps | Description |
|------|-------|-------|-------------|
| **Flow 1** | `TestIntegrationFlow1` | 4 | Create → Verify → PUT Update → Delete |
| **Flow 2** | `TestIntegrationFlow2` | 4 | Create → Verify → PATCH (firstname/lastname only) → Verify PATCH |
| **Flow 3** | `TestIntegrationFlow3` | 3 | Create → Delete → Verify Deleted (404) |
| **Flow 4** | `TestIntegrationFlow4` | 4 | Create → Validate in MySQL → Update → Verify Update in MySQL |
| **Flow 5** | `TestIntegrationFlow5_DDT` | DDT | Data-Driven: Create bookings from Excel data |

### Data-Driven Testing (DDT) - Flow 5

Flow 5 uses **TestNG DataProvider** with **Apache POI** to read test data from Excel:

```mermaid
flowchart LR
    A[Excel File] --> B[UtilExcel]
    B --> C[DataProvider]
    C --> D[Test Method]
    D --> E[Create Booking]
    E --> F[Verify Response]
```

**Excel Structure Required** (`src/test/resources/TestData.xlsx` - Sheet: "Booking"):

| firstname | lastname | totalprice | depositpaid | checkin | checkout | additionalneeds |
|-----------|----------|------------|-------------|---------|----------|-----------------|
| John | Doe | 150 | true | 2024-01-01 | 2024-01-05 | Breakfast |
| Jane | Smith | 200 | false | 2024-02-10 | 2024-02-15 | Lunch |

### TestNG Suite Configuration

```xml
<!-- testng-e2e.xml -->
<suite name="E2E Integration Test Suite" verbose="2">
    <listeners>
        <listener class-name="com.thetestingacademy.listeners.RetryListener"/>
        <listener class-name="com.thetestingacademy.listeners.TestListener"/>
    </listeners>
    <test name="E2E Integration Tests" preserve-order="true">
        <classes>
            <class name="com.thetestingacademy.tests.e2e_integration.TestIntegrationFlow1"/>
            <class name="com.thetestingacademy.tests.e2e_integration.TestIntegrationFlow2"/>
            <class name="com.thetestingacademy.tests.e2e_integration.TestIntegrationFlow3"/>
            <class name="com.thetestingacademy.tests.e2e_integration.TestIntegrationFlow4"/>
        </classes>
    </test>
    <test name="Data-Driven Tests" preserve-order="true">
        <classes>
            <class name="com.thetestingacademy.tests.e2e_integration.TestIntegrationFlow5_DDT"/>
        </classes>
    </test>
</suite>
```

---

## 🔄 Retry Analyzer & Listeners

The framework includes **automatic retry** for failed tests and **comprehensive test listeners** for logging and reporting.

### Retry Mechanism Flow

```mermaid
flowchart TD
    A[Test Execution] --> B{Test Passed?}
    B -->|Yes| C[Mark as Passed]
    B -->|No| D{Retry Count < 3?}
    D -->|Yes| E[Increment Retry Count]
    E --> F[Log Retry Attempt]
    F --> A
    D -->|No| G[Mark as Failed]
    G --> H[Log Final Failure]
```

### Listeners

| Listener | Class | Description |
|----------|-------|-------------|
| **RetryListener** | `IAnnotationTransformer` | Automatically applies RetryAnalyzer to all test methods |
| **RetryAnalyzer** | `IRetryAnalyzer` | Retries failed tests up to 3 times |
| **TestListener** | `ITestListener` | Logs test start, pass, fail, skip events |

### RetryAnalyzer Implementation

```java
public class RetryAnalyzer implements IRetryAnalyzer {
    private static final int MAX_RETRY_COUNT = 3;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            logger.warn("Retrying test '{}' - Attempt {} of {}",
                    result.getName(), retryCount, MAX_RETRY_COUNT);
            return true;
        }
        return false;
    }
}
```

### TestListener Events

```mermaid
sequenceDiagram
    participant Suite as Test Suite
    participant Listener as TestListener
    participant Test as Test Method
    participant Log as Log4j

    Suite->>Listener: onStart()
    Listener->>Log: "Suite Started"

    loop For Each Test
        Listener->>Log: onTestStart() - "Test Started"
        Test->>Test: Execute
        alt Test Passed
            Listener->>Log: onTestSuccess() - "Test Passed (Duration)"
        else Test Failed
            Listener->>Log: onTestFailure() - "Test Failed + Reason"
        else Test Skipped
            Listener->>Log: onTestSkipped() - "Test Skipped"
        end
    end

    Suite->>Listener: onFinish()
    Listener->>Log: "Suite Finished (Passed/Failed/Skipped counts)"
```

---

## 🪵 Log4j Logging

The framework uses **Log4j2** for comprehensive logging throughout test execution.

### Log Configuration

Located at `src/test/resources/log4j2.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1} - %m%n"/>
        </Console>
        <File name="FileLogger" fileName="logs/test.log">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1} - %m%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileLogger"/>
        </Root>
    </Loggers>
</Configuration>
```

### Logging Flow

```mermaid
flowchart LR
    subgraph Sources["Log Sources"]
        A[BaseTest]
        B[Test Classes]
        C[Utilities]
    end

    subgraph Log4j["Log4j2"]
        D[Logger]
    end

    subgraph Outputs["Outputs"]
        E[Console]
        F[logs/test.log]
    end

    A & B & C --> D
    D --> E & F
```

### Usage in Tests

```java
// In BaseTest (inherited by all test classes)
protected static final Logger logger = LogManager.getLogger(BaseTest.class);

// Example logging
logger.info("=== TC#INT1 - Step 1: Creating Booking ===");
logger.info("Booking created successfully with ID: {}", bookingid);
logger.debug("Auth payload: {}", payload);
logger.error("Database validation failed: {}", e.getMessage());
```

---

## 🗄 MySQL DB Integration

Use this when you want to validate API data against MySQL or seed/clean test data before a test run.

### DB Validation Flow

```mermaid
sequenceDiagram
    participant Test as Test Class
    participant API as REST API
    participant DB as MySQL Database
    participant Assert as AssertJ

    Test->>API: POST /booking
    API-->>Test: BookingResponse (ID: 123)
    Test->>DB: SELECT * FROM booking WHERE id = 123
    DB-->>Test: DB Record
    Test->>Assert: Compare API response with DB record
    Assert-->>Test: Validation Result
```

### Configure Environment Variables

Add the DB values to your local `.env` file:

```env
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=api_automation
MYSQL_USERNAME=api_user
MYSQL_PASSWORD=StrongPassword123
MYSQL_JDBC_PARAMS=useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

### Example: TestIntegrationFlow4

```java
@Test(groups = "qa", priority = 2)
@Description("TC#INT4 - Validate Booking exists in MySQL Database")
public void testValidateBookingInDatabase(ITestContext iTestContext) {
    Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");

    try (Connection connection = MySqlDBConnector.getConnection()) {
        List<Map<String, Object>> rows = MySqlDBConnector.executeSelect(
            connection,
            "SELECT booking_id, firstname, lastname FROM booking WHERE booking_id = ?",
            bookingid
        );

        assertThat(rows).isNotEmpty();
        assertThat(rows.get(0).get("firstname")).isEqualTo(expectedFirstname);
    }
}
```

---

## ⚡ Parallel Execution

Run tests in parallel using TestNG:

```xml
<suite name="All Test Suite" parallel="classes" thread-count="3">
```

### Execution Flow

```mermaid
graph TD
    A[Test Suite] --> B[Thread Pool]
    B --> C[Thread 1: Flow1]
    B --> D[Thread 2: Flow2]
    B --> E[Thread 3: Flow3]
    C & D & E --> F[Aggregate Results]
    F --> G[Allure Report]
```

---

## 📊 Allure Reporting

### Install Allure

```bash
brew install allure
```

### Generate Report

```bash
mvn clean test
allure generate allure-results --clean -o allure-report
allure open allure-report
```

### Reporting Flow

```mermaid
flowchart LR
    A[Test Execution] --> B[allure-results/]
    B --> C[allure generate]
    C --> D[allure-report/]
    D --> E[allure open]
    E --> F[HTML Dashboard]
```

### Allure Annotations Used

```java
@Owner("Promode")
@Description("TC#INT1 - Step 1. Verify that the Booking can be Created")
@Test(groups = "qa", priority = 1)
public void testCreateBooking(ITestContext iTestContext) {
    // test implementation
}
```

---

## 🔁 CI/CD Integration

This framework supports Jenkins pipeline execution.

```mermaid
flowchart TD
    A[Code Commit] --> B[Jenkins Pipeline]
    B --> C[Checkout Code]
    C --> D[mvn clean compile]
    D --> E[mvn test -Dsurefire.suiteXmlFiles=testng-e2e.xml]
    E --> F[Generate Allure Report]
    F --> G{Tests Passed?}
    G -->|Yes| H[Deploy/Notify Success]
    G -->|No| I[Notify Failure]
```

---

## 🔍 JSON Schema Validation

The framework includes **JSON Schema validation** to automatically validate API response structures against predefined schemas.

### Schema Files

Located in `src/test/resources/schemas/`:

| Schema File | Description |
|-------------|-------------|
| `booking-schema.json` | Validates GET booking response |
| `booking-response-schema.json` | Validates POST booking creation response |
| `token-response-schema.json` | Validates authentication token response |
| `error-response-schema.json` | Validates error responses |

### Validation Flow

```mermaid
flowchart LR
    A[API Response] --> B[SchemaValidator]
    B --> C[Load JSON Schema]
    C --> D{Schema Valid?}
    D -->|Yes| E[Test Passes]
    D -->|No| F[Assertion Error with Details]
```

### Usage Examples

```java
// Using convenience methods in AssertActions
assertActions.verifyBookingResponseSchema(response);
assertActions.verifyTokenResponseSchema(response);
assertActions.verifyBookingSchema(response);

// Using generic method with custom schema file
assertActions.verifyResponseSchema(response, "booking-schema.json");

// Check validity without throwing exception
boolean isValid = SchemaValidator.isValidSchema(response, SchemaValidator.Schemas.BOOKING_RESPONSE);
```

### Schema Validation Test Class

Run schema validation tests:

```bash
mvn test -Dtest=TestSchemaValidation
```

**Tests included:**
- `testBookingResponseSchema()` - Validates booking creation response
- `testTokenResponseSchema()` - Validates token response
- `testBookingSchema()` - Validates GET booking response
- `testInvalidSchemaFails()` - Verifies invalid schema detection
- `testGenericSchemaValidation()` - Tests generic validation method

---

## 🧠 Best Practices

* Use **POJOs for request/response modeling** - `Booking`, `PartialBookingUpdate`, `Auth`
* Keep **test data separate** with PayloadManager
* Use **centralized configuration** via `APIConstants` and `EnvUtil`
* Implement **Log4j logging** for debugging and traceability
* Add **assertion layers** with `AssertActions` and AssertJ
* Use **ITestContext** for sharing state between ordered test methods
* Maintain **clean folder structure** - POJOs, modules, utils, tests

---

## 📸 Screenshots

### Test Execution

![Execution](https://github.com/PramodDutta/APIAutomationRestAssured/assets/1409610/69f398b3-8798-4fba-a091-3b1e321dcc7d)

### CI/CD Pipeline

![CI/CD](https://github.com/PramodDutta/APIAutomationRestAssured/assets/1409610/2d58bf82-0ffb-4fcb-a2d9-cf26920fa7b5)

### Allure Report

![Allure](https://github.com/PramodDutta/APIAutomationFramworkATB6x/assets/1409610/79ba2093-a1b7-4b36-ba16-9a6827af7afe)

---

## 🎯 Conclusion

This framework provides a **scalable, maintainable, and production-ready solution** for API automation with:

* Clean architecture with separation of concerns
* 5 comprehensive E2E integration test flows (including DDT)
* JSON Schema validation for API response structure verification
* Log4j2 logging for debugging and traceability
* MySQL database validation support
* Powerful Allure reporting
* CI/CD readiness with Jenkins
* Extensibility for future enhancements
