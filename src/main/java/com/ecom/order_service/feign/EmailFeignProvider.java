package com.ecom.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "email-service", url = "${email-service.url}", contextId = "emailFeignProvider")
public interface EmailFeignProvider {

    @PostMapping(value = "/sendEmail", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseEntity<String> sendMail(
            @RequestParam String toEmail,
            @RequestParam String text,
            @RequestParam String subject,
            @RequestPart(required = false) MultipartFile resource);
}

