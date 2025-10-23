package com.microservices.orderservice.controller;

import com.microservices.orderservice.dto.OrderRequestDto;
import com.microservices.orderservice.dto.OrderResponseDto;
import com.microservices.orderservice.entity.Order;
import com.microservices.orderservice.exception.OrderNotFoundException;
import com.microservices.orderservice.exception.ProductNotAvailableException;
import com.microservices.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Service", description = "API para la gestión de pedidos")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido")
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequest) {
        OrderResponseDto createdOrder = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Obtener detalles de un pedido específico")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long orderId) {
        OrderResponseDto order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(
            @RequestParam(required = false) String customerEmail) {

        List<OrderResponseDto> orders;

        if (customerEmail != null && !customerEmail.isEmpty()) {
            orders = orderService.getOrdersByCustomerEmail(customerEmail);
        } else {
            orders = orderService.getAllOrders();
        }

        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Actualizar el estado de un pedido")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {

        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            OrderResponseDto updatedOrder = orderService.updateOrderStatus(orderId, orderStatus);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Estado inválido");
            error.put("message", "Los estados válidos son: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED");
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Manejadores de excepciones específicas
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleOrderNotFound(OrderNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Pedido no encontrado");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<Map<String, String>> handleProductNotAvailable(ProductNotAvailableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Producto no disponible");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");
        error.put("message", "Ha ocurrido un error inesperado");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}