package com.ecom.order_service.dto;

import com.ecom.order_service.constants.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {
    private Long id;
    private String userId;
    private Double totalAmount;
    private String shippingAddress;

    // Delivery Details
    private String deliveryName;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryZipCode;
    private String deliveryCountry;
    private String deliveryPhone;

    // Payment
    private String paymentMode;
    private String paymentId;

    private OrderStatus status;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
