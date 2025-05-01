package com.ecom.order_service.service.impl;

import com.ecom.order_service.constants.OrderStatus;
import com.ecom.order_service.dto.OrderDto;
import com.ecom.order_service.entity.Order;
import com.ecom.order_service.exception.ResourceNotFoundException;
import com.ecom.order_service.feign.CartFeignProvider;
import com.ecom.order_service.helper.OrderMapper;
import com.ecom.order_service.repository.OrderRepository;
import com.ecom.order_service.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartFeignProvider cartFeignProvider;

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        if (orderDto == null || orderDto.getUserId() == null || orderDto.getItems() == null || orderDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Invalid order data. UserId and items are required.");
        }

        try {
            log.info("Creating new order for user: {}", orderDto.getUserId());

            Order order = OrderMapper.toEntity(orderDto);
            Order savedOrder = orderRepository.save(order);

            try {
                cartFeignProvider.clearCart(orderDto.getUserId());
                log.info("Cart cleared successfully for user: {}", orderDto.getUserId());
            } catch (Exception e) {
                log.error("Failed to clear cart for user {}: {}", orderDto.getUserId(), e.getMessage());
            }

            return OrderMapper.toDto(savedOrder);

        } catch (Exception e) {
            log.error("Failed to create order for user {}: {}", orderDto.getUserId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create order. Please try again later.", e);
        }
    }


    @Override
    public OrderDto getOrderById(Long orderId) {
        log.info("Fetching order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return OrderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto updateOrder(Long orderId, OrderDto orderDto) {
        log.info("Updating order with ID: {}", orderId);
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Update fields
        if (orderDto.getUserId() != null) existingOrder.setUserId(orderDto.getUserId());
        if (orderDto.getTotalAmount() != null) existingOrder.setTotalAmount(orderDto.getTotalAmount());
        if (orderDto.getShippingAddress() != null) existingOrder.setShippingAddress(orderDto.getShippingAddress());
        if (orderDto.getPaymentMode() != null) existingOrder.setPaymentMode(orderDto.getPaymentMode());
        if (orderDto.getStatus() != null) existingOrder.setStatus(orderDto.getStatus());

        // Handle items if provided
        if (orderDto.getItems() != null && !orderDto.getItems().isEmpty()) {
            existingOrder.setItems(OrderMapper.toEntity(orderDto).getItems());
            existingOrder.getItems().forEach(item -> item.setOrder(existingOrder));
        }

        Order updatedOrder = orderRepository.save(existingOrder);
        return OrderMapper.toDto(updatedOrder);
    }

    @Override
    public void deleteOrder(Long orderId) {
        log.warn("Deleting order with ID: {}", orderId);
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        orderRepository.delete(existingOrder);
    }

    @Override
    public List<OrderDto> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        return orders.stream().map(OrderMapper::toDto).toList();
    }

    @Override
    public void updateOrderStatus(Long orderId, String orderStatus) {
        Optional<Order> order = orderRepository.findById(orderId);

        if(order.isPresent()){
            order.get().setStatus(OrderStatus.valueOf(orderStatus));
            orderRepository.save(order.get());
            return;
        }

        throw new ResourceNotFoundException("Order not found with id: " + orderId);
    }
}
