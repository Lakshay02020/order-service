package com.ecom.order_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class OrderStatusUpdateRequest {
    String orderStatus;
}
