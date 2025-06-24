package com.ecom.order_service.service;

import com.ecom.order_service.dto.OrderDetailDto;
import com.ecom.order_service.dto.OrderDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(OrderDetailDto orderDetailDto);
    OrderDto getOrderById(Long orderId);
    List<OrderDto> getAllOrders();
    OrderDto updateOrder(Long orderId, OrderDto orderDto);
    void deleteOrder(Long orderId);
    List<OrderDto> getOrdersByUserId(@PathVariable String userId);
    void updateOrderStatus(Long orderId, String orderStatus);
}
