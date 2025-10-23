package com.microservices.orderservice.client;

import com.microservices.orderservice.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.product-service.url:http://localhost:8081}")
    private String productServiceUrl;

    /**
     * Obtiene información de un producto por su ID
     */
    public ProductDto getProduct(Long productId) {
        logger.info("Obteniendo producto con ID: {} desde Product Service", productId);

        try {
            String url = productServiceUrl + "/api/products/" + productId;
            ResponseEntity<ProductDto> response = restTemplate.getForEntity(url, ProductDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Producto obtenido exitosamente: {}", response.getBody().getName());
                return response.getBody();
            } else {
                logger.warn("Respuesta no exitosa al obtener producto ID: {}", productId);
                return null;
            }
        } catch (RestClientException e) {
            logger.error("Error al comunicarse con Product Service para producto ID: {}", productId, e);
            return null;
        }
    }

    /**
     * Verifica la disponibilidad de un producto
     */
    public AvailabilityCheckResponse checkProductAvailability(Long productId, Integer quantity) {
        logger.info("Verificando disponibilidad para producto ID: {} cantidad: {}", productId, quantity);

        try {
            String url = productServiceUrl + "/api/products/check-availability";

            Map<String, Object> request = new HashMap<>();
            request.put("productId", productId);
            request.put("quantity", quantity);

            ResponseEntity<AvailabilityCheckResponse> response = restTemplate.postForEntity(
                url, request, AvailabilityCheckResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                AvailabilityCheckResponse availabilityResponse = response.getBody();
                logger.info("Verificación de disponibilidad: {} - {}",
                           availabilityResponse.isAvailable(), availabilityResponse.getMessage());
                return availabilityResponse;
            } else {
                logger.warn("Respuesta no exitosa al verificar disponibilidad para producto ID: {}", productId);
                return new AvailabilityCheckResponse(productId, null, false, quantity, 0, null, "Error en la comunicación con Product Service");
            }
        } catch (RestClientException e) {
            logger.error("Error al verificar disponibilidad para producto ID: {}", productId, e);
            return new AvailabilityCheckResponse(productId, null, false, quantity, 0, null, "Error de comunicación: " + e.getMessage());
        }
    }

    /**
     * Clase interna para la respuesta de verificación de disponibilidad
     */
    public static class AvailabilityCheckResponse {
        private Long productId;
        private String productName;
        private boolean available;
        private Integer requestedQuantity;
        private Integer availableStock;
        private java.math.BigDecimal unitPrice;
        private String message;

        // Constructores
        public AvailabilityCheckResponse() {}

        public AvailabilityCheckResponse(Long productId, String productName, boolean available,
                                        Integer requestedQuantity, Integer availableStock,
                                        java.math.BigDecimal unitPrice, String message) {
            this.productId = productId;
            this.productName = productName;
            this.available = available;
            this.requestedQuantity = requestedQuantity;
            this.availableStock = availableStock;
            this.unitPrice = unitPrice;
            this.message = message;
        }

        // Getters y Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }

        public Integer getRequestedQuantity() { return requestedQuantity; }
        public void setRequestedQuantity(Integer requestedQuantity) { this.requestedQuantity = requestedQuantity; }

        public Integer getAvailableStock() { return availableStock; }
        public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

        public java.math.BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(java.math.BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}