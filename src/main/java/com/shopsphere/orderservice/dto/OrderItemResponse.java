package com.shopsphere.orderservice.dto;

import com.shopsphere.orderservice.dto.product.ProductSummary;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

  private Long id;
  private Long orderId;
  private Integer quantity;
  private BigDecimal price;
  private ProductSummary productSummary;
}
