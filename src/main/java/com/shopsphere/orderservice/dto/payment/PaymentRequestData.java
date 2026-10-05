package com.shopsphere.orderservice.dto.payment;

import com.shopsphere.orderservice.enums.PaymentMethod;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestData {

  private Long orderId;
  private BigDecimal amount;
  private PaymentMethod paymentMethod;
  private String paymentToken;
}
