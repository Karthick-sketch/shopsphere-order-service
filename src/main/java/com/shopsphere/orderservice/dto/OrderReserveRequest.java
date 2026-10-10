package com.shopsphere.orderservice.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReserveRequest {

  private Long orderId;
  private List<OrderItemData> orderItems;
}
