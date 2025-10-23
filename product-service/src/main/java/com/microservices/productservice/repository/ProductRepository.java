package com.microservices.productservice.repository;

import com.microservices.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Busca productos por nombre (case insensitive)
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Encuentra productos con stock mayor que cero
     */
    List<Product> findByStockGreaterThan(Integer stock);

    /**
     * Encuentra productos disponibles (stock > 0)
     */
    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    List<Product> findAvailableProducts();

    /**
     * Actualiza el stock de un producto
     */
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :productId AND p.stock >= :quantity")
    int reduceStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * Verifica si hay suficiente stock para un producto
     */
    @Query("SELECT CASE WHEN p.stock >= :quantity THEN true ELSE false END FROM Product p WHERE p.id = :productId")
    Optional<Boolean> hasEnoughStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}