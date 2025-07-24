package com.ecom.order_service.controller;

import com.ecom.order_service.feign.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Slf4j
public class PingController {

    private volatile boolean isPingJobEnabled = true;

    @Autowired private CartFeignProvider cartFeignProvider;
    @Autowired private ProductFeignProvider productFeignProvider;
    @Autowired private PaymentFeignProvider paymentFeignProvider;
    @Autowired private UserFeignProvider userFeignProvider;
    @Autowired private EmailFeignProvider emailFeignProvider;

    @PostMapping("/ping-toggle")
    public void togglePing(@RequestParam boolean enable) {
        this.isPingJobEnabled = enable;
        log.info("Ping job toggled to: {}", enable);
    }

    @Scheduled(fixedRate = 7 * 60 * 1000) // every 7 minutes
    public void pingAllServices() {
        if (!isPingJobEnabled) {
            log.debug("Ping job is disabled.");
            return;
        }

        log.info("Starting scheduled ping to all services...");

        ping("Product Service", () -> productFeignProvider.ping());
        ping("Cart Service", () -> cartFeignProvider.ping());
        ping("Payment Service", () -> paymentFeignProvider.ping());
        ping("User Service", () -> userFeignProvider.ping());
        ping("Email Service", () -> emailFeignProvider.ping());

        log.info("Completed scheduled ping to all services.");
    }

    private void ping(String serviceName, Runnable pingAction) {
        try {
            log.info("Pinging {}...", serviceName);
            pingAction.run();
            log.info("Ping to {} successful.", serviceName);
        } catch (Exception e) {
            log.error("Ping to {} failed: {}", serviceName, e.getMessage(), e);
        }
    }
}
