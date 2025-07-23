package com.ecom.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductFeignProvider {
    @GetMapping("/api/products/ping")
        void ping();
}
