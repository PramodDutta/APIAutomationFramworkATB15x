# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Run all tests with default TestNG suite
mvn test

# Run E2E integration tests (5 flows including DDT)
mvn test -Dsurefire.suiteXmlFiles=testng-e2e.xml

# Run sample tests
mvn test -Dsurefire.suiteXmlFiles=testng-sample.xml

# Run a specific test class
mvn test -Dtest=com.thetestingacademy.tests.e2e_integration.TestIntegrationFlow1

# Compile without running tests
mvn clean compile

# Generate Allure report after test run
allure generate allure-results --clean -o allure-report
allure open allure-report
```

## Architecture Overview

This is a **Rest Assured + TestNG** API automation framework targeting the Restful Booker API (and VWO as a secondary project).

### Core Layers

- **BaseTest** (`src/test/java/.../base/BaseTest.java`): All test classes extend this. Sets up `RequestSpecification` with base URL and JSON content type. Provides `getToken()` helper for authenticated requests. Includes Log4j logger.

- **PayloadManager** (`src/main/java/.../modules/PayloadManager.java`): Handles JSON serialization/deserialization using Gson. Creates request payloads (with JavaFaker for dynamic data) and parses responses into POJOs. Includes `createPartialUpdatePayload()` for PATCH requests.

- **APIConstants** (`src/main/java/.../endpoints/APIConstants.java`): Centralized API endpoints and base URLs.

- **AssertActions** (`src/test/java/.../asserts/AssertActions.java`): Wrapper around TestNG/AssertJ assertions for response validation.

- **MySqlDBConnector** (`src/main/java/.../utils/MySqlDBConnector.java`): Database validation utility for MySQL queries.

### E2E Integration Test Flows

Located in `tests/e2e_integration/`:

| Flow | Class | Description |
|------|-------|-------------|
| Flow 1 | `TestIntegrationFlow1` | Create → Verify → PUT Update → Delete |
| Flow 2 | `TestIntegrationFlow2` | Create → Verify → PATCH (partial) → Verify PATCH |
| Flow 3 | `TestIntegrationFlow3` | Create → Delete → Verify Deleted (404) |
| Flow 4 | `TestIntegrationFlow4` | Create → Validate in MySQL → Update → Verify in MySQL |
| Flow 5 | `TestIntegrationFlow5_DDT` | Data-Driven: Create bookings from Excel data |

### POJOs Structure

- `pojos/restfulbook/requestPOJOs/` - Request body models (Booking, PartialBookingUpdate, Auth, Bookingdates)
- `pojos/restfulbook/responsePojos/` - Response models (BookingResponse, TokenResponse)
- `pojos/vwo/` - VWO-specific request/response models

### Configuration

- Environment variables loaded via `EnvUtil` from `.env` file (uses dotenv-java)
- Supports JVM system properties, env vars, and `.env` file (in that priority order)
- **IMPORTANT:** Copy `.env.sample` to `.env` and fill in credentials - `.env` is gitignored
- MySQL DB config uses: `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_DATABASE`, `MYSQL_USERNAME`, `MYSQL_PASSWORD`
- Log4j config at `src/test/resources/log4j2.xml` - outputs to console and `logs/test.log`
- DDT test data: `src/test/resources/TestData.xlsx` (Sheet: "Booking")

### TestNG Suites

Test suites are defined in XML files at project root:
- `testng-sample.xml` - Sample/basic tests
- `testng-e2e.xml` - End-to-end integration flows (Flow1, Flow2, Flow3, Flow4)

Parallel execution: Configure `parallel="classes" thread-count="3"` in suite XML.

### Jenkins Pipeline

`Jenkinsfile` supports parameterized builds:
- `TEST_SUITE`: Choose between `e2e`, `sample`, `all`, or `individual`
- `SPECIFIC_TEST`: Specify a test class when running individual tests
- `GENERATE_ALLURE`: Toggle Allure report generation

### Listeners

Located in `src/test/java/.../listeners/`:
- **RetryAnalyzer**: Retries failed tests up to 3 times
- **RetryListener**: Auto-applies RetryAnalyzer to all test methods
- **TestListener**: Logs test events (start, pass, fail, skip)

## Key Patterns

- **Integration tests use ITestContext**: State like `bookingid` and `token` is shared between ordered test methods via `iTestContext.setAttribute()` / `getAttribute()`
- **Allure annotations**: Use `@Owner`, `@Description` for test documentation
- **Test groups**: Tests tagged with `groups = "qa"` for filtering
- **Log4j logging**: Use `logger.info()`, `logger.debug()`, `logger.error()` inherited from BaseTest
- **@BeforeClass/@AfterClass**: Setup runs per test class (not per `<test>` tag)
- **Retry on failure**: Failed tests automatically retry up to 3 times via RetryAnalyzer
