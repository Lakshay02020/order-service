package com.ecom.order_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class PaymentDto {

    private String paymentMode;  // e.g., ONLINE_PAYMENT, COD

    @JsonProperty("razorpayPaymentId")
    private String paymentId;    // Razorpay or other payment gateway's transaction ID
}
