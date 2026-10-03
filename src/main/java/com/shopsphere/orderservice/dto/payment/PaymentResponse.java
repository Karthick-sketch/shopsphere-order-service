package com.shopsphere.orderservice.dto.payment;

import com.shopsphere.orderservice.enums.PaymentMethod;
import com.shopsphere.orderservice.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

  private Long id;
  private BigDecimal amount;
  private PaymentStatus status;
  private PaymentMethod paymentMethod;
  private LocalDateTime paidAt;
}
