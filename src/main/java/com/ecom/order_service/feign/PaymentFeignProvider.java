package com.ecom.order_service.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payment-service", url = "${payment.service.url}")
public interface PaymentFeignProvider {

    @GetMapping("/api/payment/ping")
    void ping();
}
