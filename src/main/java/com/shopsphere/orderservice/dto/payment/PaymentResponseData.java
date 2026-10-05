package com.shopsphere.orderservice.dto.payment;

import com.shopsphere.orderservice.enums.PaymentStatus;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PaymentResponseData {

  private Long userId;
  private Long orderId;
  private Long paymentId;
  private PaymentStatus status;
  private LocalDateTime paidAt;
}
