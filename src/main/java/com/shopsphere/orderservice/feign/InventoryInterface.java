package com.shopsphere.orderservice.feign;

import com.shopsphere.orderservice.config.FeignAuthConfig;
import com.shopsphere.orderservice.dto.OrderReserveRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
  name = "SHOPSPHERE-INVENTORY-SERVICE",
  configuration = FeignAuthConfig.class
)
public interface InventoryInterface {
  @PostMapping("/api/inventory/reserve")
  ResponseEntity<?> reserve(@RequestBody OrderReserveRequest request);
}
