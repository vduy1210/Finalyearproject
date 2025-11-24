@echo off
REM ========================================
REM Test Script for Java Spring Boot Application
REM ========================================

echo ========================================
echo Starting Test Suite for Spring Boot App
echo ========================================
echo.

REM Navigate to the web-backend directory
cd /d "%~dp0web-backend"

echo Current directory: %CD%
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    pause
    exit /b 1
)

echo Maven found: 
call mvn -version
echo.

REM Clean previous build
echo ========================================
echo Step 1: Cleaning previous builds...
echo ========================================
call mvn clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven clean failed
    pause
    exit /b 1
)
echo Clean completed successfully
echo.

REM Compile the application
echo ========================================
echo Step 2: Compiling the application...
echo ========================================
call mvn compile
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)
echo Compilation completed successfully
echo.

REM Run all tests
echo ========================================
echo Step 3: Running all tests...
echo ========================================
call mvn test
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: Some tests failed
    echo Check the test reports in target/surefire-reports/
) else (
    echo All tests passed successfully!
)
echo.

REM Generate test report
echo ========================================
echo Step 4: Generating test coverage report...
echo ========================================
call mvn surefire-report:report
echo.

REM Display test results location
echo ========================================
echo Test Results Location:
echo ========================================
echo HTML Report: %CD%\target\surefire-reports\index.html
echo XML Reports: %CD%\target\surefire-reports\
echo.

REM Run specific test categories
echo ========================================
echo Step 5: Running specific test categories...
echo ========================================

echo.
echo --- Running User Controller Tests ---
call mvn test -Dtest=UserControllerTest
echo.

echo --- Running Product Controller Tests ---
call mvn test -Dtest=ProductControllerTest
echo.

echo --- Running Order Controller Tests ---
call mvn test -Dtest=OrderControllerTest
echo.

echo --- Running Integration Tests ---
call mvn test -Dtest=ApplicationIntegrationTest
echo.

REM Summary
echo ========================================
echo Test Execution Complete!
echo ========================================
echo.
echo Test Summary:
echo - Unit Tests: UserControllerTest, ProductControllerTest, OrderControllerTest
echo - Integration Tests: ApplicationIntegrationTest
echo.
echo To view detailed reports, open:
echo %CD%\target\surefire-reports\index.html
echo.

pause
