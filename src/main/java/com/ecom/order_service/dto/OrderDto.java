package com.ecom.order_service.dto;

import com.ecom.order_service.constants.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private String userId;
    private Double totalAmount;
    private String shippingAddress;
    private String paymentMode;
    private OrderStatus status;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
