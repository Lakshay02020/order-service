package com.ecom.order_service.mapper;

import com.ecom.order_service.constants.PaymentMode;
import com.ecom.order_service.dto.OrderDetailDto;
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
                .paymentMode(String.valueOf(order.getPaymentMode()))
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .shippingAddress(order.getShippingAddress())

                // Delivery Details
                .deliveryName(order.getDeliveryName())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryCity(order.getDeliveryCity())
                .deliveryState(order.getDeliveryState())
                .deliveryZipCode(order.getDeliveryZipCode())
                .deliveryCountry(order.getDeliveryCountry())
                .deliveryPhone(order.getDeliveryPhone())

                .items(order.getItems().stream()
                        .map(item -> OrderItemDto.builder()
                                .productId(item.getProductId())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static Order toEntity(OrderDetailDto dto) {
        Order order = Order.builder()
                .userId(dto.getOrderDto().getUserId())
                .totalAmount(dto.getOrderDto().getTotalAmount())

                // Delivery Details
                .deliveryName(dto.getDeliveryDto().getFullName())
                .deliveryAddress(dto.getDeliveryDto().getStreetAddress())
                .deliveryCity(dto.getDeliveryDto().getCity())
                .deliveryState(dto.getDeliveryDto().getState())
                .deliveryZipCode(dto.getDeliveryDto().getDeliveryZip())
                .deliveryCountry(dto.getDeliveryDto().getCountry())
                .deliveryPhone(dto.getDeliveryDto().getPhone())
                .deliveryEmail(dto.getDeliveryDto().getEmail())

                // Payment Details
                .paymentMode(PaymentMode.valueOf(dto.getPaymentDto().getPaymentMode()))
                .razorpayId(dto.getPaymentDto().getPaymentId())
                .build();

        List<OrderItem> items = dto.getOrderDto().getItems().stream()
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
