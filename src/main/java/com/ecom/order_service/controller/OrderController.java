package com.ecom.order_service.controller;

import com.ecom.order_service.dto.OrderDto;
import com.ecom.order_service.dto.OrderStatusUpdateRequest;
import com.ecom.order_service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 1. Place Order
    @PostMapping
    public ResponseEntity<OrderDto> placeOrder(@RequestBody OrderDto orderDto) {
        log.info("Placing new order for user: {}", orderDto.getUserId());
        return ResponseEntity.ok(orderService.createOrder(orderDto));
    }

    // 2. Get Order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        log.info("Fetching order with ID: {}", orderId);
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // 3. Get All Orders by User
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable String userId) {
        log.info("Fetching orders for user: {}", userId);
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    // 4. Update Order Status
    @PutMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId,
                                                    @RequestBody OrderStatusUpdateRequest request) {
        log.info("Updating status of order ID {} to {}", orderId, request.getOrderStatus());
        orderService.updateOrderStatus(orderId, request.getOrderStatus());
        return ResponseEntity.ok("Order status updated successfully.");
    }

    // 5. Cancel/Delete Order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        log.info("Deleting order with ID: {}", orderId);
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Order deleted successfully.");
    }
}
