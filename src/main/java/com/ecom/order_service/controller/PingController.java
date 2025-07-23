package com.ecom.order_service.controller;

import com.ecom.order_service.feign.CartFeignProvider;
import com.ecom.order_service.feign.ProductFeignProvider;
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


    // TODO understand volatile meaning here
    private volatile boolean isPingJobEnabled = true;

    @Autowired
    CartFeignProvider cartFeignProvider;

    @Autowired
    ProductFeignProvider productFeignProvider;

    @PostMapping("/ping-toggle")
    public void togglePing(@RequestParam boolean enable) {
        this.isPingJobEnabled = enable;
        log.info("Ping job toggled to: {}", enable);
    }

    @Scheduled(fixedRate = 7 * 60 * 1000)
    public void pingProductService() {
        if (!isPingJobEnabled) {
            log.debug("Ping job is disabled.");
            return;
        }

        try {
            log.info("Pinging product service...");
            productFeignProvider.ping();
            log.info("Ping to product service successful.");
        } catch (Exception e) {
            log.error("Error occurred while pinging product service: {}", e.getMessage(), e);
        }
    }
}
