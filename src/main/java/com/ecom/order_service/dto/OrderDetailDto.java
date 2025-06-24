package com.ecom.order_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDetailDto {

    private OrderDto orderDto;
    private DeliveryDto deliveryDto;
    private PaymentDto paymentDto;
}
