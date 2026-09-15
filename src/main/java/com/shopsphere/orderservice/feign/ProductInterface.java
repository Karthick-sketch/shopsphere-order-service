package com.shopsphere.orderservice.feign;

import com.shopsphere.orderservice.dto.product.*;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("SHOPSPHERE-PRODUCT-SERVICE")
public interface ProductInterface {
  @PostMapping("/api/products/summary/bash")
  ResponseEntity<List<ProductSummary>> getSummary(
    @RequestBody ProductIdsRequest ids
  );
}
