package com.microservices.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class OrderRequestDto {

    @Email(message = "Debe proporcionar un email válido")
    @NotBlank(message = "El email del cliente es obligatorio")
    private String customerEmail;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String customerName;

    @Valid
    @NotEmpty(message = "Debe incluir al menos un item en el pedido")
    private List<OrderItemRequestDto> items;

    // Constructores
    public OrderRequestDto() {
    }

    public OrderRequestDto(String customerEmail, String customerName, List<OrderItemRequestDto> items) {
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.items = items;
    }

    // Getters y Setters
    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<OrderItemRequestDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDto> items) {
        this.items = items;
    }
}