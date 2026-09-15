package com.shopsphere.orderservice.dto;

import com.shopsphere.orderservice.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

  private Long id;
  private OrderStatus status;
  private LocalDateTime placedAt;
  private BigDecimal subtotal;
  private BigDecimal shipping;
  private BigDecimal total;
  private String shippingName;
  private String shippingAddress;
  private String cardLast4;
  private List<OrderItemResponse> items;
  private Long userId;
}
