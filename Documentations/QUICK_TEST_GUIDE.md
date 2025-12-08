# Quick Test Guide

## 🚀 Quick Start

### Run All Tests (Windows)
```cmd
run-tests.bat
```

### Run All Tests (Linux/Mac)
```bash
chmod +x run-tests.sh
./run-tests.sh
```

### Run Tests with Maven
```bash
cd web-backend
mvn clean test
```

## 📋 Test Categories

### 1️⃣ User Authentication Tests (5 tests)
```bash
mvn test -Dtest=UserControllerTest
```
- Valid login
- Invalid email
- Invalid password
- Missing credentials

### 2️⃣ Product Management Tests (13 tests)
```bash
mvn test -Dtest=ProductControllerTest
```
- Get all products
- Get product by ID
- Update product
- Update stock
- Upload images
- Validation checks

### 3️⃣ Order Processing Tests (9 tests)
```bash
mvn test -Dtest=OrderControllerTest
```
- Place orders
- Stock validation
- Multiple items
- Customer management

### 4️⃣ Integration Tests (6 tests)
```bash
mvn test -Dtest=ApplicationIntegrationTest
```
- Complete workflows
- End-to-end scenarios
- Database transactions

## 📊 View Test Results

After running tests, open:
```
web-backend/target/surefire-reports/index.html
```

## 🔍 Test Coverage

| Feature | Tests | Status |
|---------|-------|--------|
| Login | 5 | ✅ |
| Products | 13 | ✅ |
| Orders | 9 | ✅ |
| Integration | 6 | ✅ |
| **TOTAL** | **33** | ✅ |

## 💡 Common Commands

```bash
# Clean and test
mvn clean test

# Test specific class
mvn test -Dtest=UserControllerTest

# Test with logging
mvn test -X

# Skip tests (when building)
mvn clean install -DskipTests

# Generate coverage report
mvn clean test jacoco:report
```

## 🐛 Troubleshooting

**Problem**: Tests fail with database error
**Solution**: Check `application-test.properties` configuration

**Problem**: Maven not found
**Solution**: Install Maven and add to PATH

**Problem**: Port already in use
**Solution**: Tests use H2 in-memory DB (no port conflicts)

## 📝 Test Files Location

```
web-backend/src/test/java/com/example/saleapp/web_backend/
├── controller/
│   ├── UserControllerTest.java
│   ├── ProductControllerTest.java
│   └── OrderControllerTest.java
└── integration/
    └── ApplicationIntegrationTest.java
```

## ✅ Before Pushing Code

1. Run all tests: `mvn clean test`
2. Check test reports
3. Fix any failures
4. Commit changes

---
**Total Test Cases**: 33  
**Framework**: JUnit 5 + Spring Boot Test  
**Coverage**: All Controllers + Integration Flows
