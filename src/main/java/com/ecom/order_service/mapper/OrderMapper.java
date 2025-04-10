package com.ecom.order_service.helper;

import com.ecom.order_service.dto.OrderDto;
import com.ecom.order_service.dto.OrderItemDto;
import com.ecom.order_service.entity.Order;
import com.ecom.order_service.entity.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .paymentMode(order.getPaymentMode())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream()
                        .map(item -> OrderItemDto.builder()
                                .productId(item.getProductId())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static Order toEntity(OrderDto dto) {
        Order order = Order.builder()
                .userId(dto.getUserId())
                .totalAmount(dto.getTotalAmount())
                .shippingAddress(dto.getShippingAddress())
                .paymentMode(dto.getPaymentMode())
                .status(dto.getStatus())
                .build();

        List<OrderItem> items = dto.getItems().stream()
                .map(itemDto -> OrderItem.builder()
                        .productId(itemDto.getProductId())
                        .quantity(itemDto.getQuantity())
                        .price(itemDto.getPrice())
                        .order(order) // set bidirectional mapping
                        .build())
                .collect(Collectors.toList());

        order.setItems(items);
        return order;
    }
}
