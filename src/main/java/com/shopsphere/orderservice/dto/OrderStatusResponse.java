package com.shopsphere.orderservice.dto;

import com.shopsphere.orderservice.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusResponse {

  private Long id;
  private OrderStatus status;
}
