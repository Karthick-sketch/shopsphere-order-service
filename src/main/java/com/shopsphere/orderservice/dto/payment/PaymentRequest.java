package com.shopsphere.orderservice.dto.payment;

import com.shopsphere.orderservice.enums.PaymentMethod;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

  private Long orderId;
  private BigDecimal amount;
  private PaymentMethod paymentMethod;
  private String cardName;
  private String cardNumber;
  private String expiryMonth;
  private String expiryYear;
  private String cvv;
}
