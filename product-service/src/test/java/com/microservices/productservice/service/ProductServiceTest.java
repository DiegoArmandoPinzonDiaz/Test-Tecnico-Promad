package com.microservices.productservice.service;

import com.microservices.productservice.dto.AvailabilityCheckRequestDto;
import com.microservices.productservice.dto.AvailabilityCheckResponseDto;
import com.microservices.productservice.dto.ProductRequestDto;
import com.microservices.productservice.dto.ProductResponseDto;
import com.microservices.productservice.entity.Product;
import com.microservices.productservice.exception.ProductNotFoundException;
import com.microservices.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testCreateProduct_Success() {
        // Arrange
        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setName("Test Product");
        requestDto.setDescription("Test Description");
        requestDto.setPrice(BigDecimal.valueOf(99.99));
        requestDto.setStock(10);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Test Product");
        savedProduct.setDescription("Test Description");
        savedProduct.setPrice(BigDecimal.valueOf(99.99));
        savedProduct.setStock(10);
        savedProduct.setCreatedAt(LocalDateTime.now());
        savedProduct.setUpdatedAt(LocalDateTime.now());

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        ProductResponseDto result = productService.createProduct(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(BigDecimal.valueOf(99.99), result.getPrice());
        assertEquals(10, result.getStock());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testGetProductById_Success() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setStock(10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        ProductResponseDto result = productService.getProductById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Test Product", result.getName());

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(productId);
        });

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testCheckAvailability_Available() {
        // Arrange
        Long productId = 1L;
        Integer requestedQuantity = 5;

        AvailabilityCheckRequestDto requestDto = new AvailabilityCheckRequestDto();
        requestDto.setProductId(productId);
        requestDto.setQuantity(requestedQuantity);

        Product product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setStock(10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        AvailabilityCheckResponseDto result = productService.checkAvailability(requestDto);

        // Assert
        assertNotNull(result);
        assertTrue(result.isAvailable());
        assertEquals(productId, result.getProductId());
        assertEquals("Test Product", result.getProductName());
        assertEquals(requestedQuantity, result.getRequestedQuantity());
        assertEquals(10, result.getAvailableStock());
        assertEquals(BigDecimal.valueOf(99.99), result.getUnitPrice());
        assertEquals("Producto disponible", result.getMessage());

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testCheckAvailability_InsufficientStock() {
        // Arrange
        Long productId = 1L;
        Integer requestedQuantity = 15; // Más de lo disponible

        AvailabilityCheckRequestDto requestDto = new AvailabilityCheckRequestDto();
        requestDto.setProductId(productId);
        requestDto.setQuantity(requestedQuantity);

        Product product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setStock(10); // Menos de lo solicitado

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        AvailabilityCheckResponseDto result = productService.checkAvailability(requestDto);

        // Assert
        assertNotNull(result);
        assertFalse(result.isAvailable());
        assertEquals(productId, result.getProductId());
        assertEquals(requestedQuantity, result.getRequestedQuantity());
        assertEquals(10, result.getAvailableStock());
        assertEquals("Stock insuficiente. Disponible: 10", result.getMessage());

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testCheckAvailability_ProductNotFound() {
        // Arrange
        Long productId = 999L;
        Integer requestedQuantity = 5;

        AvailabilityCheckRequestDto requestDto = new AvailabilityCheckRequestDto();
        requestDto.setProductId(productId);
        requestDto.setQuantity(requestedQuantity);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        AvailabilityCheckResponseDto result = productService.checkAvailability(requestDto);

        // Assert
        assertNotNull(result);
        assertFalse(result.isAvailable());
        assertEquals(productId, result.getProductId());
        assertNull(result.getProductName());
        assertEquals(requestedQuantity, result.getRequestedQuantity());
        assertEquals(0, result.getAvailableStock());
        assertNull(result.getUnitPrice());
        assertEquals("Producto no encontrado", result.getMessage());

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testReduceStock_Success() {
        // Arrange
        Long productId = 1L;
        Integer quantity = 5;

        when(productRepository.reduceStock(productId, quantity)).thenReturn(1);

        // Act
        boolean result = productService.reduceStock(productId, quantity);

        // Assert
        assertTrue(result);
        verify(productRepository, times(1)).reduceStock(productId, quantity);
    }

    @Test
    void testReduceStock_Failed() {
        // Arrange
        Long productId = 1L;
        Integer quantity = 15;

        when(productRepository.reduceStock(productId, quantity)).thenReturn(0);

        // Act
        boolean result = productService.reduceStock(productId, quantity);

        // Assert
        assertFalse(result);
        verify(productRepository, times(1)).reduceStock(productId, quantity);
    }
}