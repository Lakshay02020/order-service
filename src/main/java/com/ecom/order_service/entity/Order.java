package com.ecom.order_service.entity;

import com.ecom.order_service.constants.OrderStatus;
import com.ecom.order_service.constants.PaymentMode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String deliveryEmail;

    // Payment Details
    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;
    private String razorpayId;  // Razorpay payment id, etc.

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
