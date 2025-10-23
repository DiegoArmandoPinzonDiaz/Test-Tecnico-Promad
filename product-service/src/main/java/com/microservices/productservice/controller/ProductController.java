package com.microservices.productservice.controller;

import com.microservices.productservice.dto.AvailabilityCheckRequestDto;
import com.microservices.productservice.dto.AvailabilityCheckResponseDto;
import com.microservices.productservice.dto.ProductRequestDto;
import com.microservices.productservice.dto.ProductResponseDto;
import com.microservices.productservice.exception.ProductNotFoundException;
import com.microservices.productservice.service.ProductService;
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
@RequestMapping("/api/products")
@Tag(name = "Product Service", description = "API para la gestión de productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    @Operation(summary = "Crear un nuevo producto")
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequest) {
        ProductResponseDto createdProduct = productService.createProduct(productRequest);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Obtener detalles de un producto")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId) {
        ProductResponseDto product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PostMapping("/check-availability")
    @Operation(summary = "Verificar disponibilidad de productos")
    public ResponseEntity<AvailabilityCheckResponseDto> checkAvailability(
            @Valid @RequestBody AvailabilityCheckRequestDto availabilityRequest) {
        AvailabilityCheckResponseDto response = productService.checkAvailability(availabilityRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Actualizar un producto existente")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequestDto productRequest) {
        ProductResponseDto updatedProduct = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Eliminar un producto")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Producto eliminado exitosamente");
        response.put("productId", productId.toString());
        return ResponseEntity.ok(response);
    }

    // Manejador de excepciones específicas
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound(ProductNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Producto no encontrado");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}