# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

Requires **JDK 23+** (Jenkinsfile pins `JDK23`) and Maven.

```bash
# Run tests with surefire defaults (no suiteXmlFiles configured in pom.xml;
# runs any *Test class on the test classpath)
mvn test

# Run E2E integration tests (4 CRUD flows + 1 DDT flow)
mvn test -Dsurefire.suiteXmlFiles=testng-e2e.xml

# Run sample tests
mvn test -Dsurefire.suiteXmlFiles=testng-sample.xml

# Run a single test class
mvn test -Dtest=com.thetestingacademy.tests.e2e_integration.TestIntegrationFlow1

# Run a single test method
mvn test -Dtest=TestIntegrationFlow1#testCreateBooking

# Compile without running tests
mvn clean compile

# Generate & open Allure report after test run
allure generate allure-results --clean -o allure-report
allure open allure-report
```

## Architecture Overview

**Rest Assured + TestNG** API automation framework. Primary target is the Restful Booker API; VWO is a secondary SUT with its own payload manager and POJOs.

### Core Layers

- **BaseTest** (`src/test/java/.../base/BaseTest.java`) — every test class extends this. Builds a `RequestSpecification` with base URL + JSON content-type, exposes `getToken()` for authenticated calls, and provides the shared Log4j `logger`.
- **PayloadManager** (`src/main/java/.../modules/PayloadManager.java`) — Gson-backed serialization/deserialization. Builds request payloads (with JavaFaker for dynamic data) and parses responses into POJOs. Includes `createPartialUpdatePayload()` for PATCH. VWO has its own `modules/vwo/VWOPayloadManager.java`.
- **APIConstants** (`src/main/java/.../endpoints/APIConstants.java`) — centralized endpoint URIs and base URLs.
- **AssertActions** (`src/test/java/.../asserts/AssertActions.java`) — wrapper over TestNG/AssertJ assertions; also provides `verifyBookingResponseSchema()`, `verifyTokenResponseSchema()`, etc. for schema validation.
- **SchemaValidator** (`src/test/java/.../utils/SchemaValidator.java`) — wraps `io.rest-assured:json-schema-validator`. Schemas live in `src/test/resources/schemas/` (`booking-schema.json`, `booking-response-schema.json`, `token-response-schema.json`, `error-response-schema.json`). A `SchemaValidator.Schemas` enum exposes canonical names.
- **MySqlDBConnector** (`src/main/java/.../utils/MySqlDBConnector.java`) — utility for validating API results against MySQL (used in Flow 4).
- **Config readers** — `EnvUtil` (dotenv-java), `PropertiesReader` (`data.properties`), `YamlReader` (`config.yaml`).
- **Excel utilities** — `src/test/java/.../utilExcels/UtilExcel.java` reads DDT data via Apache POI; `TestDataGenerator` seeds `TestData.xlsx`.

### Test Categories

| Location | Purpose |
|---|---|
| `tests/e2e_integration/` | Multi-step ordered flows (Flow1–Flow5_DDT) — state shared via `ITestContext` |
| `tests/individual/` | Single-endpoint tests (e.g. `TestCreateBooking`, `TestCreateToken`, `TestHealthCheck`) |
| `tests/individual/vwo/` | VWO API tests (`TestVWOLoginInvalid`) |
| `tests/schema/` | `TestSchemaValidation` — exercises all JSON schemas |
| `tests/sample/` | Basic smoke tests referenced from `testng-sample.xml` |

### E2E Integration Test Flows

| Flow | Class | Description |
|------|-------|-------------|
| 1 | `TestIntegrationFlow1` | Create → Verify → PUT → Delete |
| 2 | `TestIntegrationFlow2` | Create → Verify → PATCH → Verify PATCH |
| 3 | `TestIntegrationFlow3` | Create → Delete → Verify 404 |
| 4 | `TestIntegrationFlow4` | Create → Validate in MySQL → Update → Verify in MySQL |
| 5 | `TestIntegrationFlow5_DDT` | Data-driven: bookings from `TestData.xlsx` (sheet "Booking") |

### POJOs

- `pojos/restfulbook/requestPOJOs/` — `Booking`, `PartialBookingUpdate`, `Auth`, `Bookingdates`
- `pojos/restfulbook/responsePojos/` — `BookingResponse`, `TokenResponse`, `InvalidTokenResponse`
- `pojos/vwo/requestPOJO/` — `VWOLoginRequest`
- `pojos/vwo/responsePojo/` — `InvalidLoginResponse`

### Configuration & Environment

- Config is loaded via `EnvUtil` in priority order: **JVM system properties → OS env vars → `.env` file** (dotenv-java).
- Copy `.env.sample` → `.env` before running tests that need credentials. `.env` is gitignored.
- MySQL keys used by `MySqlDBConnector`: `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_DATABASE`, `MYSQL_USERNAME`, `MYSQL_PASSWORD`, `MYSQL_JDBC_PARAMS`.
- Log4j2 config: `src/test/resources/log4j2.xml` → writes to console and `logs/test.log`.
- DDT data: `src/test/resources/TestData.xlsx`, sheet `Booking`.

### TestNG Suites & Listeners

Suite XMLs live at project root: `testng-sample.xml`, `testng-e2e.xml`. Both register:

- **RetryListener** (`IAnnotationTransformer`) — auto-applies `RetryAnalyzer` to every `@Test`.
- **RetryAnalyzer** (`IRetryAnalyzer`) — retries failed tests up to **3 times**.
- **TestListener** (`ITestListener`) — logs start/pass/fail/skip events with durations.

For parallel execution, set `parallel="classes" thread-count="N"` on the `<suite>` element.

### Jenkins Pipeline

`Jenkinsfile` is parameterized:
- `TEST_SUITE`: `e2e` | `sample` | `all` | `individual`
- `SPECIFIC_TEST`: fully-qualified test class (used only when `TEST_SUITE=individual`)
- `GENERATE_ALLURE`: toggle report generation

Post-build: JUnit results archived from `target/surefire-reports/*.xml`, logs from `logs/*.log`, Slack notifications to `#api-automation` on success/failure.

## Key Patterns

- **Ordered flows share state via `ITestContext`** — e.g. `iTestContext.setAttribute("bookingid", id)` in step 1, `getAttribute("bookingid")` in step 2. Methods use `priority = N` to enforce order.
- **Auth**: call `getToken()` from `BaseTest` once per flow; cache it in the test context if reused.
- **Allure annotations**: `@Owner`, `@Description` on every test; group under `groups = "qa"`.
- **Retries are automatic** — don't add manual retry logic; rely on `RetryAnalyzer`.
- **Schema validation** — prefer `AssertActions.verifyXxxSchema(response)` over inline `matchesJsonSchemaInClasspath(...)` calls so schema paths stay centralized.
- **Logging** — use `logger.info/debug/error` inherited from `BaseTest`; don't call `System.out.println`.
- **`@BeforeClass`/`@AfterClass`** run per test class, not per `<test>` tag — keep expensive setup (token fetch, DB connection) there.

## Test Results Dashboard

A React + SQLite dashboard lives under `dashboard/`. It reads `target/surefire-reports/testng-results.xml` and shows pass/fail counts, per-run drilldowns, daily trends, and top failing tests behind a login. See `dashboard/README.md` for setup.

```bash
# After any mvn test run:
node dashboard/ingest/ingest.js    # inserts a new run into dashboard/backend/data/dashboard.sqlite

# Backend + UI (dev)
cd dashboard/backend  && npm start       # http://localhost:4000
cd dashboard/frontend && npm run dev     # http://localhost:5173  (default login: admin / admin123)
```

The ingest script writes directly to SQLite (no HTTP dependency), so it can be wired as a Jenkins post-test stage safely.
