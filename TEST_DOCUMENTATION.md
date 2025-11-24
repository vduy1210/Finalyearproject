# Test Suite Documentation

## Overview
This test suite provides comprehensive testing for the Spring Boot Sale Application, including unit tests for all controllers and integration tests for complete application flows.

## Test Structure

### 1. Unit Tests

#### UserControllerTest
Tests authentication and login functionality:
- ✅ Successful login with valid credentials
- ✅ Login failure with invalid email
- ✅ Login failure with invalid password
- ✅ Login failure with missing email
- ✅ Login failure with missing password

**Total: 5 test cases**

#### ProductControllerTest
Tests product management operations:
- ✅ Get all products
- ✅ Get product by ID (success)
- ✅ Get product by ID (not found)
- ✅ Update product (success)
- ✅ Update product (not found)
- ✅ Update product stock (success)
- ✅ Update product stock with negative value
- ✅ Update product stock (product not found)
- ✅ Upload product image (success)
- ✅ Upload empty image file
- ✅ Upload image for non-existent product
- ✅ Check product image endpoint
- ✅ Test upload directory status

**Total: 13 test cases**

#### OrderControllerTest
Tests order processing functionality:
- ✅ Place order successfully
- ✅ Place order with non-existent product
- ✅ Place order with insufficient stock
- ✅ Place order with table number
- ✅ Place order for existing customer
- ✅ Place order when default staff not found
- ✅ Get orders by user (success)
- ✅ Get orders by user (user not found)
- ✅ Place order with multiple items

**Total: 9 test cases**

### 2. Integration Tests

#### ApplicationIntegrationTest
Tests complete application workflows:
- ✅ User login flow (end-to-end)
- ✅ Product management flow (CRUD operations)
- ✅ Complete order flow (with stock reduction)
- ✅ Order with insufficient stock validation
- ✅ Multiple products in single order
- ✅ Existing customer order update

**Total: 6 test cases**

## Test Coverage Summary

| Component | Test Cases | Coverage |
|-----------|------------|----------|
| UserController | 5 | Login, Authentication |
| ProductController | 13 | CRUD, Stock, Images |
| OrderController | 9 | Order Processing, Validation |
| Integration | 6 | Complete Workflows |
| **TOTAL** | **33** | **All Features** |

## Running Tests

### Option 1: Using the Test Script (Windows)
```cmd
run-tests.bat
```

### Option 2: Using the Test Script (Linux/Mac)
```bash
chmod +x run-tests.sh
./run-tests.sh
```

### Option 3: Using Maven Directly
```bash
# Run all tests
cd web-backend
mvn test

# Run specific test class
mvn test -Dtest=UserControllerTest
mvn test -Dtest=ProductControllerTest
mvn test -Dtest=OrderControllerTest
mvn test -Dtest=ApplicationIntegrationTest

# Run with coverage report
mvn clean test jacoco:report

# Generate HTML test report
mvn surefire-report:report
```

### Option 4: Run from IDE
1. Open the project in IntelliJ IDEA or Eclipse
2. Navigate to test class
3. Right-click and select "Run Tests"

## Test Reports

After running tests, reports are generated in:
- **HTML Report**: `web-backend/target/surefire-reports/index.html`
- **XML Reports**: `web-backend/target/surefire-reports/*.xml`
- **Console Output**: Displays test results in real-time

## Test Configuration

### Test Database
Tests use H2 in-memory database configured in:
- `src/test/resources/application-test.properties`

Configuration:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

### Test Dependencies
Required in `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Test Scenarios Covered

### Authentication Flow
1. User login with valid credentials
2. Invalid email/password handling
3. Missing credentials validation

### Product Management
1. List all products
2. View product details
3. Update product information
4. Manage product stock
5. Upload product images
6. Image validation

### Order Processing
1. Create new order
2. Calculate order total
3. Reduce product stock automatically
4. Customer creation/update
5. Multiple items per order
6. Stock validation
7. Table number assignment

### Integration Scenarios
1. Complete user journey from login to order
2. Multi-product orders
3. Stock management across orders
4. Customer data persistence
5. Order history tracking

## Assertions Used

- Status code validation
- JSON response structure
- Data accuracy checks
- Database state verification
- Error message validation
- Business logic validation

## Best Practices Followed

✅ Arrange-Act-Assert pattern
✅ Descriptive test names
✅ Independent test cases
✅ Mock external dependencies
✅ Test data setup/teardown
✅ Clear assertion messages
✅ Edge case testing
✅ Integration test isolation

## Continuous Integration

These tests can be integrated with CI/CD pipelines:

### GitHub Actions Example
```yaml
name: Run Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '21'
      - run: cd web-backend && mvn test
```

## Troubleshooting

### Common Issues

1. **Tests fail due to database connection**
   - Ensure H2 database is in test dependencies
   - Check application-test.properties configuration

2. **MockMvc context not loading**
   - Verify @WebMvcTest annotation
   - Check @MockBean declarations

3. **Integration tests failing**
   - Ensure @SpringBootTest is used
   - Check if test database schema matches

4. **File upload tests failing**
   - Verify uploads directory permissions
   - Check multipart configuration

## Extending Tests

To add new tests:

1. Create test class in appropriate package
2. Follow naming convention: `*Test.java`
3. Use `@DisplayName` for readable test names
4. Include setup and teardown if needed
5. Update this documentation

## Contact & Support

For issues or questions about tests:
- Check test output in console
- Review HTML test reports
- Verify test configuration
- Check application logs

---

**Last Updated**: November 2025
**Test Framework**: JUnit 5, Spring Boot Test, MockMvc
**Total Test Cases**: 33
