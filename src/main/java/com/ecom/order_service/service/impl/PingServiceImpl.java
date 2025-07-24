package com.ecom.order_service.service.impl;

import com.ecom.order_service.feign.*;
import com.ecom.order_service.service.PingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PingServiceImpl implements PingService {
    @Autowired private CartFeignProvider cartFeignProvider;
    @Autowired private ProductFeignProvider productFeignProvider;
    @Autowired private PaymentFeignProvider paymentFeignProvider;
    @Autowired private UserFeignProvider userFeignProvider;
    @Autowired private EmailFeignProvider emailFeignProvider;
    boolean isPingJobEnabled = true;

    @Override
    @Scheduled(fixedRate = 7 * 60 * 1000)
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

    @Async
    public void ping(String serviceName, Runnable pingAction) {
        try {
            log.info("Pinging {}...", serviceName);
            pingAction.run();
            log.info("Ping to {} successful.", serviceName);
        } catch (Exception e) {
            log.error("Ping to {} failed: {}", serviceName, e.getMessage(), e);
        }
    }
}
