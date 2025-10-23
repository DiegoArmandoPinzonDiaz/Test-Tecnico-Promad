package com.microservices.orderservice.service;

import com.microservices.orderservice.client.ProductServiceClient;
import com.microservices.orderservice.dto.*;
import com.microservices.orderservice.entity.Order;
import com.microservices.orderservice.entity.OrderItem;
import com.microservices.orderservice.exception.OrderNotFoundException;
import com.microservices.orderservice.exception.ProductNotAvailableException;
import com.microservices.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductServiceClient productServiceClient;

    /**
     * Crear un nuevo pedido
     */
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        logger.info("Creando nuevo pedido para cliente: {}", requestDto.getCustomerEmail());

        // Validar disponibilidad de productos
        List<ProductValidationResult> validationResults = validateProductsAvailability(requestDto.getItems());

        // Verificar si hay productos no disponibles
        List<ProductValidationResult> unavailableProducts = validationResults.stream()
                .filter(result -> !result.isAvailable())
                .collect(Collectors.toList());

        if (!unavailableProducts.isEmpty()) {
            String errorMessage = "Productos no disponibles: " +
                unavailableProducts.stream()
                    .map(ProductValidationResult::getErrorMessage)
                    .collect(Collectors.joining(", "));
            logger.warn("Error en creación de pedido: {}", errorMessage);
            throw new ProductNotAvailableException(errorMessage);
        }

        // Crear el pedido
        Order order = new Order(requestDto.getCustomerEmail(), requestDto.getCustomerName());

        // Agregar items al pedido
        for (ProductValidationResult validation : validationResults) {
            OrderItem item = new OrderItem(
                validation.getProductId(),
                validation.getProductName(),
                validation.getRequestedQuantity(),
                validation.getUnitPrice()
            );
            order.addItem(item);
        }

        // Guardar el pedido
        Order savedOrder = orderRepository.save(order);
        logger.info("Pedido creado exitosamente con ID: {}", savedOrder.getId());

        return new OrderResponseDto(savedOrder);
    }

    /**
     * Obtener pedido por ID
     */
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long id) {
        logger.info("Buscando pedido con ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Pedido no encontrado con ID: " + id));

        return new OrderResponseDto(order);
    }

    /**
     * Obtener todos los pedidos
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        logger.info("Obteniendo todos los pedidos");

        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtener pedidos por email del cliente
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByCustomerEmail(String customerEmail) {
        logger.info("Obteniendo pedidos para cliente: {}", customerEmail);

        List<Order> orders = orderRepository.findByCustomerEmail(customerEmail);
        return orders.stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar estado del pedido
     */
    public OrderResponseDto updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        logger.info("Actualizando estado del pedido ID: {} a {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Pedido no encontrado con ID: " + orderId));

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        logger.info("Estado del pedido actualizado exitosamente: {}", updatedOrder.getId());
        return new OrderResponseDto(updatedOrder);
    }

    /**
     * Validar disponibilidad de productos antes de crear el pedido
     */
    private List<ProductValidationResult> validateProductsAvailability(List<OrderItemRequestDto> items) {
        logger.info("Validando disponibilidad de {} productos", items.size());

        List<ProductValidationResult> results = new ArrayList<>();

        for (OrderItemRequestDto item : items) {
            ProductServiceClient.AvailabilityCheckResponse availability =
                productServiceClient.checkProductAvailability(item.getProductId(), item.getQuantity());

            ProductValidationResult result = new ProductValidationResult();
            result.setProductId(item.getProductId());
            result.setRequestedQuantity(item.getQuantity());

            if (availability != null && availability.isAvailable()) {
                result.setAvailable(true);
                result.setProductName(availability.getProductName());
                result.setUnitPrice(availability.getUnitPrice());
                result.setAvailableStock(availability.getAvailableStock());
            } else {
                result.setAvailable(false);
                result.setErrorMessage(availability != null ? availability.getMessage() :
                    "Error al comunicarse con el servicio de productos");
            }

            results.add(result);
        }

        return results;
    }

    /**
     * Clase interna para resultados de validación de productos
     */
    private static class ProductValidationResult {
        private Long productId;
        private String productName;
        private Integer requestedQuantity;
        private Integer availableStock;
        private BigDecimal unitPrice;
        private boolean available;
        private String errorMessage;

        // Getters y Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public Integer getRequestedQuantity() { return requestedQuantity; }
        public void setRequestedQuantity(Integer requestedQuantity) { this.requestedQuantity = requestedQuantity; }

        public Integer getAvailableStock() { return availableStock; }
        public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}