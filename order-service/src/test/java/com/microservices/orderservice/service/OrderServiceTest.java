package com.microservices.orderservice.service;

import com.microservices.orderservice.client.ProductServiceClient;
import com.microservices.orderservice.dto.OrderItemRequestDto;
import com.microservices.orderservice.dto.OrderRequestDto;
import com.microservices.orderservice.dto.OrderResponseDto;
import com.microservices.orderservice.entity.Order;
import com.microservices.orderservice.exception.OrderNotFoundException;
import com.microservices.orderservice.exception.ProductNotAvailableException;
import com.microservices.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testCreateOrder_Success() {
        // Arrange
        OrderItemRequestDto item1 = new OrderItemRequestDto(1L, 2);
        OrderItemRequestDto item2 = new OrderItemRequestDto(2L, 1);

        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setCustomerEmail("test@example.com");
        requestDto.setCustomerName("Test Customer");
        requestDto.setItems(Arrays.asList(item1, item2));

        // Mock availability responses
        ProductServiceClient.AvailabilityCheckResponse availability1 =
            new ProductServiceClient.AvailabilityCheckResponse(
                1L, "Product 1", true, 2, 10, BigDecimal.valueOf(50.00), "Producto disponible"
            );

        ProductServiceClient.AvailabilityCheckResponse availability2 =
            new ProductServiceClient.AvailabilityCheckResponse(
                2L, "Product 2", true, 1, 5, BigDecimal.valueOf(100.00), "Producto disponible"
            );

        when(productServiceClient.checkProductAvailability(1L, 2)).thenReturn(availability1);
        when(productServiceClient.checkProductAvailability(2L, 1)).thenReturn(availability2);

        Order savedOrder = new Order("test@example.com", "Test Customer");
        savedOrder.setId(1L);
        savedOrder.setStatus(Order.OrderStatus.PENDING);
        savedOrder.setTotalAmount(BigDecimal.valueOf(200.00));
        savedOrder.setCreatedAt(LocalDateTime.now());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        OrderResponseDto result = orderService.createOrder(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getCustomerEmail());
        assertEquals("Test Customer", result.getCustomerName());
        assertEquals("PENDING", result.getStatus());

        verify(productServiceClient, times(1)).checkProductAvailability(1L, 2);
        verify(productServiceClient, times(1)).checkProductAvailability(2L, 1);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateOrder_ProductNotAvailable() {
        // Arrange
        OrderItemRequestDto item = new OrderItemRequestDto(1L, 10);

        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setCustomerEmail("test@example.com");
        requestDto.setCustomerName("Test Customer");
        requestDto.setItems(Arrays.asList(item));

        // Mock unavailable product
        ProductServiceClient.AvailabilityCheckResponse availability =
            new ProductServiceClient.AvailabilityCheckResponse(
                1L, "Product 1", false, 10, 5, BigDecimal.valueOf(50.00), "Stock insuficiente. Disponible: 5"
            );

        when(productServiceClient.checkProductAvailability(1L, 10)).thenReturn(availability);

        // Act & Assert
        assertThrows(ProductNotAvailableException.class, () -> {
            orderService.createOrder(requestDto);
        });

        verify(productServiceClient, times(1)).checkProductAvailability(1L, 10);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrderById_Success() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order("test@example.com", "Test Customer");
        order.setId(orderId);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.valueOf(100.00));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        OrderResponseDto result = orderService.getOrderById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals("test@example.com", result.getCustomerEmail());
        assertEquals("Test Customer", result.getCustomerName());
        assertEquals("PENDING", result.getStatus());

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testGetOrderById_NotFound() {
        // Arrange
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrderById(orderId);
        });

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order("test@example.com", "Test Customer");
        order.setId(orderId);
        order.setStatus(Order.OrderStatus.PENDING);

        Order updatedOrder = new Order("test@example.com", "Test Customer");
        updatedOrder.setId(orderId);
        updatedOrder.setStatus(Order.OrderStatus.CONFIRMED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        // Act
        OrderResponseDto result = orderService.updateOrderStatus(orderId, Order.OrderStatus.CONFIRMED);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals("CONFIRMED", result.getStatus());

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        // Arrange
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.updateOrderStatus(orderId, Order.OrderStatus.CONFIRMED);
        });

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }
}