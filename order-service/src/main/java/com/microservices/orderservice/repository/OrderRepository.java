package com.microservices.orderservice.repository;

import com.microservices.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Encuentra pedidos por email del cliente
     */
    List<Order> findByCustomerEmail(String customerEmail);

    /**
     * Encuentra pedidos por estado
     */
    List<Order> findByStatus(Order.OrderStatus status);

    /**
     * Encuentra pedidos por email y estado
     */
    List<Order> findByCustomerEmailAndStatus(String customerEmail, Order.OrderStatus status);

    /**
     * Encuentra pedidos creados después de una fecha específica
     */
    List<Order> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Encuentra pedidos por rango de fechas
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    /**
     * Cuenta pedidos por estado
     */
    long countByStatus(Order.OrderStatus status);

    /**
     * Encuentra los últimos N pedidos ordenados por fecha de creación
     */
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findLatestOrders();
}