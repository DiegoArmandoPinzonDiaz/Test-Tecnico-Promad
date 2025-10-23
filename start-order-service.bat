@echo off
echo =========================================
echo Starting Order Service on port 8080
echo =========================================
cd order-service
mvn clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b 1
)
echo.
echo Build successful, starting application...
echo Access Swagger UI at: http://localhost:8080/swagger-ui.html
echo Access H2 Console at: http://localhost:8080/h2-console
echo.
echo IMPORTANT: Make sure Product Service is running on port 8081
echo.
mvn spring-boot:run