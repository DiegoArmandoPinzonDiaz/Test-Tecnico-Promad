package com.microservices.productservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class AvailabilityCheckRequestDto {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor que 0")
    private Integer quantity;

    // Constructores
    public AvailabilityCheckRequestDto() {
    }

    public AvailabilityCheckRequestDto(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters y Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}