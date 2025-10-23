@echo off
echo =========================================
echo Starting Product Service on port 8081
echo =========================================
cd product-service
mvn clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b 1
)
echo.
echo Build successful, starting application...
echo Access Swagger UI at: http://localhost:8081/swagger-ui.html
echo Access H2 Console at: http://localhost:8081/h2-console
echo.
mvn spring-boot:run