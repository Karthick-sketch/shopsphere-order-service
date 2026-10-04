package com.shopsphere.orderservice.feign;

import com.shopsphere.orderservice.config.FeignAuthConfig;
import com.shopsphere.orderservice.dto.payment.PaymentRequest;
import com.shopsphere.orderservice.dto.payment.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
  name = "SHOPSPHERE-PAYMENT-SERVICE",
  configuration = FeignAuthConfig.class
)
public interface PaymentInterface {
  @PostMapping("/api/payments")
  ResponseEntity<PaymentResponse> processPayment(
    @RequestBody PaymentRequest paymentRequest
  );
}
