package com.microservices.productservice.dto;

import java.math.BigDecimal;

public class AvailabilityCheckResponseDto {

    private Long productId;
    private String productName;
    private boolean available;
    private Integer requestedQuantity;
    private Integer availableStock;
    private BigDecimal unitPrice;
    private String message;

    // Constructores
    public AvailabilityCheckResponseDto() {
    }

    public AvailabilityCheckResponseDto(Long productId, String productName, boolean available,
                                       Integer requestedQuantity, Integer availableStock,
                                       BigDecimal unitPrice, String message) {
        this.productId = productId;
        this.productName = productName;
        this.available = available;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
        this.unitPrice = unitPrice;
        this.message = message;
    }

    // Getters y Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}