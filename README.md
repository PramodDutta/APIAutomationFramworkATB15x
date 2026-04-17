# 🚀 API Automation Framework – Rest Assured (Java)

### ✍️ Author: Prrammod Dutta

A robust **API Automation Framework** built using **Rest Assured** for testing the CRUD operations of the **Restful Booker API**. This framework follows a **hybrid design pattern**, integrates CI/CD pipelines, and generates rich **Allure Reports**.

---

## 📌 Table of Contents

* [Overview](#-overview)
* [Tech Stack](#-tech-stack)
* [Architecture](#-architecture)
* [Framework Flow](#-framework-flow)
* [Project Structure](#-project-structure)
* [Setup & Execution](#-setup--execution)
* [Parallel Execution](#-parallel-execution)
* [Integration Tests](#-integration-tests)
* [Allure Reporting](#-allure-reporting)
* [CI/CD Integration](#-cicd-integration)
* [Test Scenarios](#-test-scenarios)
* [Best Practices](#-best-practices)

---

## 📖 Overview

This framework is designed to:

* Automate **CRUD operations** of REST APIs
* Provide **scalable and maintainable test architecture**
* Support **parallel execution**
* Integrate with **CI/CD pipelines**
* Generate **detailed reports with Allure**

---

## 🛠 Tech Stack

* ☕ Java (JDK > 23)
* 🔗 Rest Assured
* 🧪 TestNG
* 📦 Maven
* 📊 Apache POI (for test data)
* 🔍 AssertJ (advanced assertions)
* 🔄 Jackson API & GSON (JSON parsing)
* 🪵 Log4j (logging)
* 📈 Allure Reports
* ⚙️ Jenkins (CI/CD)

---

## 🏗 Architecture

The framework follows a **Hybrid Framework Design** combining:

* Page Object Model (API abstraction)
* Data-driven testing
* Utility-based reusable components

```mermaid
flowchart TD
    A[TestNG Test Cases] --> B[API Layer]
    B --> C[Request Builder]
    B --> D[Response Handler]
    C --> E[Rest Assured Client]
    E --> F[API Server]
    F --> D
    D --> G[Assertions (AssertJ)]
    G --> H[Reports (Allure)]
```

---

## 🔄 Framework Flow

```mermaid
sequenceDiagram
    participant Test as TestNG Test
    participant API as API Layer
    participant RA as Rest Assured
    participant Server as API Server
    participant Report as Allure

    Test->>API: Build Request
    API->>RA: Send HTTP Request
    RA->>Server: API Call
    Server-->>RA: Response
    RA-->>API: Parsed Response
    API-->>Test: Return Data
    Test->>Report: Log Results
```

---

## 📁 Project Structure

```mermaid
graph TD
    A[src/test/java] --> B[tests]
    A --> C[api]
    A --> D[utils]
    A --> E[base]
    A --> F[pojos]
    A --> G[constants]

    B --> B1[Test Cases]
    C --> C1[API Methods]
    D --> D1[Reusable Utilities]
    E --> E1[Base Test Setup]
    F --> F1[Request/Response Models]
    G --> G1[Framework Constants]
```

---

## ⚙️ Setup & Execution

### 🔹 Prerequisites

* Install Java (JDK 23+)
* Install Maven

### 🔹 Run Tests

```bash
mvn test -Dsurefire.suiteXmlFiles=testng.xml
```

### 🔹 Add Maven Surefire Plugin

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>3.3.0</version>
      <configuration>
        <suiteXmlFiles>
          <suiteXmlFile>${suiteXmlFile}</suiteXmlFile>
        </suiteXmlFiles>
      </configuration>
    </plugin>
  </plugins>
</build>
```

---

## ⚡ Parallel Execution

Run tests in parallel using TestNG:

```xml
<suite name="All Test Suite" parallel="methods" thread-count="2">
```

### Execution Flow

```mermaid
graph LR
    A[Test Suite] --> B[Test 1]
    A --> C[Test 2]
    B --> D[Thread 1]
    C --> E[Thread 2]
```

---

## 🔗 Integration Tests

Run full integration suite:

```bash
mvn clean test -DsuiteXmlFile=testng-integration.xml
```

### Covered Scenarios:

* Create Booking
* Generate Token
* Update Booking
* Delete Booking
* Validate End-to-End Flow

---

## 📊 Allure Reporting

### 🔹 Install Allure

```bash
brew install allure
```

### 🔹 Add Dependency

```xml
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-testng</artifactId>
    <version>2.13.0</version>
</dependency>
```

### 🔹 Add Plugin

```xml
<plugin>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-maven</artifactId>
    <version>2.10.0</version>
</plugin>
```

### 🔹 Generate Report

```bash
mvn clean test
allure generate target/allure-results --clean -o allure-report
allure open allure-report
```

### Reporting Flow

```mermaid
flowchart LR
    A[Test Execution] --> B[Allure Results]
    B --> C[Generate Report]
    C --> D[HTML Dashboard]
```

---

## 🔁 CI/CD Integration

This framework supports Jenkins pipeline execution.

```mermaid
flowchart TD
    A[Code Commit] --> B[Jenkins Pipeline]
    B --> C[Build]
    C --> D[Run Tests]
    D --> E[Generate Report]
    E --> F[Publish Results]
```

---

## 🧪 Test Scenarios

### ✅ CRUD Operations

1. Create Booking → Update → Verify
2. Create Booking → Delete → Verify عدم existence
3. Get Booking → Update → Validate
4. Create → Delete Flow
5. Invalid Payload Testing
6. Update Deleted Resource

---

### 📬 Postman Assignments

* Create Collections for:

    * RESTful Booker CRUD
    * Add test scripts
    * Integration scenarios

---

### 🔍 Validation Checks

* ✅ Response Body
* ✅ Status Code
* ✅ Headers

---

## 🧠 Best Practices

* Use **POJOs for request/response modeling**
* Keep **test data separate**
* Use **centralized configuration**
* Implement **logging for debugging**
* Add **assertion layers**
* Maintain **clean folder structure**

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

* Clean architecture
* Powerful reporting
* CI/CD readiness
* Extensibility for future enhancements

---

If you want, I can also:

* Add badges (build status, coverage)
* Improve GitHub UI sections
* Add sample test cases or code snippets
* Convert this into a portfolio-ready project README
