# Sistema de Gestión de Pedidos con Microservicios

## 📋 Descripción

Este proyecto implementa un sistema distribuido backend compuesto por dos microservicios que gestionan pedidos de una tienda en línea. Los microservicios se comunican entre sí a través de APIs REST siguiendo las mejores prácticas de desarrollo backend.

## 🏗️ Arquitectura

El sistema está compuesto por dos microservicios independientes:

### 🛍️ **Product Service** (Puerto 8081)
- **Responsabilidades:**
  - Gestionar el catálogo de productos
  - Controlar el inventario/stock de productos
  - Actualizar stock cuando se realizan pedidos
  - Proporcionar información de productos al Order Service

### 📦 **Order Service** (Puerto 8080)
- **Responsabilidades:**
  - Gestionar el ciclo de vida de los pedidos
  - Validar disponibilidad de productos antes de crear pedidos
  - Comunicarse con el Product Service para verificar stock
  - Mantener el estado de los pedidos

## 🛠️ Stack Tecnológico

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Base de datos H2** (en memoria)
- **Maven** como gestor de dependencias
- **JUnit 5** para pruebas unitarias
- **Mockito** para mocking
- **SpringDoc OpenAPI** para documentación de APIs
- **RestTemplate** para comunicación entre microservicios

## 🚀 Cómo Ejecutar

### Prerequisitos
- Java 17 o superior
- Maven 3.6 o superior

### Pasos para ejecutar

1. **Clonar el repositorio** (si aplica) o navegar al directorio de microservicios:
   ```bash
   cd microservicios
   ```

2. **Ejecutar Product Service:**
   ```bash
   cd product-service
   mvn clean install
   mvn spring-boot:run
   ```

   Si tiene problemas con sus variables de entorno pueden usar:
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"; $env:Path = "C:\Program Files\Java\jdk-17\bin;C:\Program Files\Apache\maven\mvn\bin;" + $env:Path; cd "C:\Users\6114049\Downloads\Microservicios Prueba\microservicios\product-service"; mvn clean package -DskipTests; mvn spring-boot:run

   $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
   $env:Path = "C:\Program Files\Java\jdk-17\bin;C:\Program Files\Apache\maven\mvn\bin;" + $env:Path
    cd "C:\Users\6114049\Downloads\Microservicios Prueba\microservicios\order-service"
    mvn clean install
    mvn spring-boot:run


   El servicio estará disponible en: http://localhost:8081

3. **Ejecutar Order Service** (en otra terminal):
   ```bash
   cd order-service
   mvn clean install
   mvn spring-boot:run
   ```
   El servicio estará disponible en: http://localhost:8080

## 📚 Documentación de APIs

### Product Service API
- **Swagger UI:** http://localhost:8081/swagger-ui.html

### Order Service API
- **Swagger UI:** http://localhost:8080/swagger-ui.html


## 📊 Base de Datos

### Product Service - H2 Database
- **URL:** `jdbc:h2:mem:productdb`
- **Usuario:** `sa`
- **Contraseña:** `password`

### Order Service - H2 Database
- **URL:** `jdbc:h2:mem:orderdb`
- **Usuario:** `sa`
- **Contraseña:** `password`

## 🧪 Pruebas

### Ejecutar pruebas unitarias

**Product Service:**
```bash
cd product-service
mvn test
```

**Order Service:**
```bash
cd order-service
mvn test
```

### Ejecutar todas las pruebas
```bash
mvn test -f product-service/pom.xml
mvn test -f order-service/pom.xml
```

## 🏃‍♂️ Flujo de Trabajo

1. **Product Service** mantiene el catálogo de productos con stock
2. **Order Service** recibe una solicitud de pedido
3. **Order Service** consulta al **Product Service** para verificar disponibilidad
4. Si hay stock suficiente, se crea el pedido
5. El pedido pasa por diferentes estados: PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED

## 📈 Estados de Pedido

- **PENDING**: Pedido recibido, pendiente de confirmación
- **CONFIRMED**: Pedido confirmado, stock reservado
- **PROCESSING**: Pedido en proceso de preparación
- **SHIPPED**: Pedido enviado
- **DELIVERED**: Pedido entregado
- **CANCELLED**: Pedido cancelado


Uso
### 1. Obtener todos los productos
GET http://localhost:8081/api/products


### 2. Obtener un producto específico
GET http://localhost:8081/api/products/1

### 3. Crear un nuevo producto
POST http://localhost:8081/api/products \
Headers: "Content-Type: application/json"
payload: 
  '{
    "name": "Monitor 4K Samsung",
    "description": "Monitor 4K Ultra HD de 32 pulgadas",
    "price": 599.99,
    "stock": 8
  }'

### 4. Actualizar un producto existente
PUT http://localhost:8081/api/products/1
Headers: "Content-Type: application/json"
payload: 
  '{
    "name": "Laptop Dell XPS 13 Updated",
    "description": "Laptop ultraligera con procesador Intel Core i7, 16GB RAM, 1TB SSD",
    "price": 1399.99,
    "stock": 12
  }'


### 5. Verificar disponibilidad de producto
POST http://localhost:8081/api/products/check-availability
Headers: "Content-Type: application/json"
payload: 
  '{
    "productId": 1,
    "quantity": 3
  }'

### 6. Eliminar un producto
DELETE http://localhost:8081/api/products/11



### ORDENES
### 1. Obtener todos los pedidos
GET http://localhost:8080/api/orders

### 2. Obtener un pedido específico
GET http://localhost:8080/api/orders/1


### 3. Crear un nuevo pedido 
POST http://localhost:8080/api/orders
Headers: "Content-Type: application/json"
payload: 
  '{
    "customerEmail": "juan.perez@example.com",
    "customerName": "Juan Pérez",
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ]
  }'


### 4. Actualizar estado de un pedido
PUT "http://localhost:8080/api/orders/1/status?status=CONFIRMED"

### 5: Verificar productos disponibles
GET http://localhost:8081/api/products



