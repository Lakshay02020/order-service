package com.ecom.order_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private String productId;
    private Integer quantity;
    private Double price;
}