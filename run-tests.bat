@echo off
echo =========================================
echo Running All Tests
echo =========================================

echo.
echo Running Product Service Tests...
echo =========================================
cd product-service
mvn test
set PRODUCT_TEST_RESULT=%ERRORLEVEL%

cd ..
echo.
echo Running Order Service Tests...
echo =========================================
cd order-service
mvn test
set ORDER_TEST_RESULT=%ERRORLEVEL%

cd ..
echo.
echo =========================================
echo TEST RESULTS SUMMARY
echo =========================================

if %PRODUCT_TEST_RESULT% EQU 0 (
    echo Product Service Tests: PASSED
) else (
    echo Product Service Tests: FAILED
)

if %ORDER_TEST_RESULT% EQU 0 (
    echo Order Service Tests: PASSED
) else (
    echo Order Service Tests: FAILED
)

echo =========================================

if %PRODUCT_TEST_RESULT% NEQ 0 exit /b 1
if %ORDER_TEST_RESULT% NEQ 0 exit /b 1

echo All tests passed successfully!
pause