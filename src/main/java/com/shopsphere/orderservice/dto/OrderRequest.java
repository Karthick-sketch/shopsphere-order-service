package com.shopsphere.orderservice.dto;

import com.shopsphere.orderservice.dto.payment.PaymentDetails;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class OrderRequest {

  private BigDecimal subtotal;
  private BigDecimal shipping;
  private BigDecimal total;
  private String shippingName;
  private String shippingAddress;
  private String cardLast4;
  private List<OrderItemRequest> orderItems;
  private PaymentDetails paymentDetails;
}
