package com.ecom.order_service.controller;

import com.ecom.order_service.dto.OrderDetailDto;
import com.ecom.order_service.dto.OrderDto;
import com.ecom.order_service.dto.OrderStatusUpdateRequest;
import com.ecom.order_service.feign.EmailFeignProvider;
import com.ecom.order_service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")

public class OrderController {

    @Autowired
    private OrderService orderService;

    // 1. Place Order
    @PostMapping("/placeOrder")
    public ResponseEntity<OrderDto> placeOrder(@RequestBody OrderDetailDto orderDetailDto) {
        log.info("Placing new order for user: {}", orderDetailDto.getOrderDto().getUserId());
        return ResponseEntity.ok(orderService.createOrder(orderDetailDto));
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

    EmailFeignProvider emailFeignProvider;
    @GetMapping("/ping")
    public String ping(){
        emailFeignProvider.sendMail("lakshay02singla@gmail.com", "sad", "sub", null);
        return "SENT";
    }
}
