package com.ecom.order_service.controller;

import com.ecom.order_service.feign.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Slf4j
public class PingController {

    private volatile boolean isPingJobEnabled = true;

    @GetMapping("/ping")
    public void ping(){
        log.info("Ping Recieved");
    }

    @PostMapping("/ping-toggle")
    public void togglePing(@RequestParam boolean enable) {
        this.isPingJobEnabled = enable;
        log.info("Ping job toggled to: {}", enable);
    }

    @Async
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
