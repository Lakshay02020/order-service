package com.ecom.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service", url = "${user.service.url}") // e.g., http://localhost:8081/api/user
public interface UserFeignProvider {

    @GetMapping("/api/auth/ping")
    void ping();
}
