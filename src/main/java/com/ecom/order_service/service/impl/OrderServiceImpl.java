package com.ecom.order_service.service.impl;

import com.ecom.order_service.constants.OrderStatus;
import com.ecom.order_service.dto.DeliveryDto;
import com.ecom.order_service.dto.OrderDetailDto;
import com.ecom.order_service.dto.OrderDto;
import com.ecom.order_service.dto.PaymentDto;
import com.ecom.order_service.entity.Order;
import com.ecom.order_service.exception.ResourceNotFoundException;
import com.ecom.order_service.feign.CartFeignProvider;
import com.ecom.order_service.feign.EmailFeignProvider;
import com.ecom.order_service.mapper.OrderMapper;
import com.ecom.order_service.repository.OrderRepository;
import com.ecom.order_service.service.OrderService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartFeignProvider cartFeignProvider;
    private final EmailFeignProvider emailFeignProvider;

    @Override
    @Transactional
    public OrderDto createOrder(OrderDetailDto orderDetailDto) {
        log.info(orderDetailDto.toString());
        OrderDto orderDto = orderDetailDto.getOrderDto();
        log.info(orderDetailDto.getDeliveryDto().getDeliveryZip());
        validateData(orderDetailDto);

        try {
            Order order = OrderMapper.toEntity(orderDetailDto);
            order.setStatus(OrderStatus.PLACED);
            Order savedOrder = orderRepository.save(order);

            log.info("Send email after successfully placing the order");
            notifyCustomerViaEmail(order);
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

    @Async
    private void notifyCustomerViaEmail(Order order) {
//        String email = order.getCustomerEmail(); // or use a fixed one for testing
        String email = order.getDeliveryEmail();
        String subject = "🛒 Order Confirmation - Order #" + order.getId();

        String body = String.format(
                "Dear Customer,\n\n"
                        + "Thank you for placing an order with us! 🎉\n\n"
                        + "Here are your order details:\n"
                        + "---------------------------------\n"
                        + "Order ID       : %s\n"
                        + "Order Date     : %s\n"
                        + "Payment Mode   : %s\n"
                        + "Payment Status : %s\n"
                        + "Order Amount    : ₹%.2f\n"
                        + "Total Items Ordered  : %d item(s)\n\n"
                        + "Shipping Address:\n%s\n\n"
                        + "We'll notify you again once your order is shipped. 🚚\n\n"
                        + "Thanks for shopping with us!\n"
                        + "Team %s",
                order.getId(),
                order.getCreatedAt(),
                order.getPaymentMode(),
                Objects.equals(order.getPaymentMode().toString(), "COD") ? "PENDING" : "PAID",
                order.getTotalAmount(),
                order.getItems().size(),
                order.getDeliveryAddress(),
                "The Carpet Factory"
        );

        log.info(body);
        try{
        emailFeignProvider.sendMail(email, body, subject, null);
        }catch (FeignException.FeignClientException exception){
            log.info("Feign Exception");
        }
    }

    void validateData(OrderDetailDto orderDetailDto){
        // Basic Order Checks
        OrderDto orderDto = orderDetailDto.getOrderDto();
        if (orderDto == null || orderDto.getUserId() == null || orderDto.getItems() == null || orderDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Invalid order data. UserId and items are required.");
        }

        // Delivery Checks
        DeliveryDto deliveryDto = orderDetailDto.getDeliveryDto();
        log.info(deliveryDto.toString());

        if (deliveryDto == null ||
                deliveryDto.getFullName() == null || deliveryDto.getFullName().isEmpty() ||
                deliveryDto.getCity() == null || deliveryDto.getCity().isEmpty() ||
                deliveryDto.getState() == null || deliveryDto.getState().isEmpty() ||
                deliveryDto.getDeliveryZip() == null || deliveryDto.getDeliveryZip().isEmpty() ||
                deliveryDto.getCountry() == null || deliveryDto.getCountry().isEmpty() ||
                deliveryDto.getPhone() == null || deliveryDto.getPhone().isEmpty()) {
            throw new IllegalArgumentException("Incomplete delivery details. All delivery fields are required.");
        }

        // Payment Checks
        PaymentDto paymentDto = orderDetailDto.getPaymentDto();
        log.info(paymentDto.toString());
        log.info( paymentDto.getPaymentMode(), paymentDto.getPaymentId());
        if (paymentDto == null ||
                paymentDto.getPaymentMode() == null || paymentDto.getPaymentMode().isEmpty() ||
                paymentDto.getPaymentId() == null || paymentDto.getPaymentId().isEmpty()) {
            throw new IllegalArgumentException("Invalid payment details. Payment mode and payment ID are required.");
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

        // Update allow for status field only
        if (orderDto.getStatus() != null)
            existingOrder.setStatus(orderDto.getStatus());

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
