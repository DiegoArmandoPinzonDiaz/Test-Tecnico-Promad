package com.microservices.productservice.service;

import com.microservices.productservice.dto.AvailabilityCheckRequestDto;
import com.microservices.productservice.dto.AvailabilityCheckResponseDto;
import com.microservices.productservice.dto.ProductRequestDto;
import com.microservices.productservice.dto.ProductResponseDto;
import com.microservices.productservice.entity.Product;
import com.microservices.productservice.exception.ProductNotFoundException;
import com.microservices.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    /**
     * Crear un nuevo producto
     */
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        logger.info("Creando nuevo producto: {}", requestDto.getName());

        Product product = new Product();
        product.setName(requestDto.getName());
        product.setDescription(requestDto.getDescription());
        product.setPrice(requestDto.getPrice());
        product.setStock(requestDto.getStock());

        Product savedProduct = productRepository.save(product);
        logger.info("Producto creado con ID: {}", savedProduct.getId());

        return new ProductResponseDto(savedProduct);
    }

    /**
     * Obtener producto por ID
     */
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        logger.info("Buscando producto con ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con ID: " + id));

        return new ProductResponseDto(product);
    }

    /**
     * Obtener todos los productos
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        logger.info("Obteniendo todos los productos");

        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Verificar disponibilidad de producto
     */
    @Transactional(readOnly = true)
    public AvailabilityCheckResponseDto checkAvailability(AvailabilityCheckRequestDto requestDto) {
        logger.info("Verificando disponibilidad para producto ID: {} cantidad: {}",
                   requestDto.getProductId(), requestDto.getQuantity());

        Optional<Product> productOpt = productRepository.findById(requestDto.getProductId());

        if (productOpt.isEmpty()) {
            logger.warn("Producto no encontrado con ID: {}", requestDto.getProductId());
            return new AvailabilityCheckResponseDto(
                    requestDto.getProductId(),
                    null,
                    false,
                    requestDto.getQuantity(),
                    0,
                    null,
                    "Producto no encontrado"
            );
        }

        Product product = productOpt.get();
        boolean isAvailable = product.getStock() >= requestDto.getQuantity();
        String message = isAvailable ?
            "Producto disponible" :
            "Stock insuficiente. Disponible: " + product.getStock();

        logger.info("Disponibilidad verificada: {} - {}", isAvailable, message);

        return new AvailabilityCheckResponseDto(
                product.getId(),
                product.getName(),
                isAvailable,
                requestDto.getQuantity(),
                product.getStock(),
                product.getPrice(),
                message
        );
    }

    /**
     * Reducir stock de un producto (usado cuando se confirma un pedido)
     */
    public boolean reduceStock(Long productId, Integer quantity) {
        logger.info("Reduciendo stock para producto ID: {} cantidad: {}", productId, quantity);

        int updatedRows = productRepository.reduceStock(productId, quantity);
        boolean success = updatedRows > 0;

        if (success) {
            logger.info("Stock reducido exitosamente para producto ID: {}", productId);
        } else {
            logger.warn("No se pudo reducir el stock para producto ID: {} - stock insuficiente o producto no encontrado", productId);
        }

        return success;
    }

    /**
     * Actualizar producto
     */
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        logger.info("Actualizando producto con ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con ID: " + id));

        product.setName(requestDto.getName());
        product.setDescription(requestDto.getDescription());
        product.setPrice(requestDto.getPrice());
        product.setStock(requestDto.getStock());

        Product updatedProduct = productRepository.save(product);
        logger.info("Producto actualizado exitosamente: {}", updatedProduct.getId());

        return new ProductResponseDto(updatedProduct);
    }

    /**
     * Eliminar producto
     */
    public void deleteProduct(Long id) {
        logger.info("Eliminando producto con ID: {}", id);

        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Producto no encontrado con ID: " + id);
        }

        productRepository.deleteById(id);
        logger.info("Producto eliminado exitosamente: {}", id);
    }
}