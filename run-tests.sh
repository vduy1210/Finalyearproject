#!/bin/bash
# ========================================
# Test Script for Java Spring Boot Application (Linux/Mac)
# ========================================

echo "========================================"
echo "Starting Test Suite for Spring Boot App"
echo "========================================"
echo ""

# Navigate to the web-backend directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR/web-backend"

echo "Current directory: $(pwd)"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven and add it to your PATH"
    exit 1
fi

echo "Maven found:"
mvn -version
echo ""

# Clean previous build
echo "========================================"
echo "Step 1: Cleaning previous builds..."
echo "========================================"
mvn clean
if [ $? -ne 0 ]; then
    echo "ERROR: Maven clean failed"
    exit 1
fi
echo "Clean completed successfully"
echo ""

# Compile the application
echo "========================================"
echo "Step 2: Compiling the application..."
echo "========================================"
mvn compile
if [ $? -ne 0 ]; then
    echo "ERROR: Compilation failed"
    exit 1
fi
echo "Compilation completed successfully"
echo ""

# Run all tests
echo "========================================"
echo "Step 3: Running all tests..."
echo "========================================"
mvn test
if [ $? -ne 0 ]; then
    echo "WARNING: Some tests failed"
    echo "Check the test reports in target/surefire-reports/"
else
    echo "All tests passed successfully!"
fi
echo ""

# Generate test report
echo "========================================"
echo "Step 4: Generating test coverage report..."
echo "========================================"
mvn surefire-report:report
echo ""

# Display test results location
echo "========================================"
echo "Test Results Location:"
echo "========================================"
echo "HTML Report: $(pwd)/target/surefire-reports/index.html"
echo "XML Reports: $(pwd)/target/surefire-reports/"
echo ""

# Run specific test categories
echo "========================================"
echo "Step 5: Running specific test categories..."
echo "========================================"

echo ""
echo "--- Running User Controller Tests ---"
mvn test -Dtest=UserControllerTest
echo ""

echo "--- Running Product Controller Tests ---"
mvn test -Dtest=ProductControllerTest
echo ""

echo "--- Running Order Controller Tests ---"
mvn test -Dtest=OrderControllerTest
echo ""

echo "--- Running Integration Tests ---"
mvn test -Dtest=ApplicationIntegrationTest
echo ""

# Summary
echo "========================================"
echo "Test Execution Complete!"
echo "========================================"
echo ""
echo "Test Summary:"
echo "- Unit Tests: UserControllerTest, ProductControllerTest, OrderControllerTest"
echo "- Integration Tests: ApplicationIntegrationTest"
echo ""
echo "To view detailed reports, open:"
echo "$(pwd)/target/surefire-reports/index.html"
echo ""
