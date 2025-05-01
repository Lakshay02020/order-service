package com.ecom.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service", url = "${cart.service.url}") // e.g., http://localhost:8120/api/cart
public interface CartFeignProvider {

    @DeleteMapping("/api/cart/clearItems/{userId}")
    void clearCart(@PathVariable("userId") String userId);
}