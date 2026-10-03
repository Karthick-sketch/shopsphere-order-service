package com.shopsphere.orderservice.dto.payment;

import com.shopsphere.orderservice.enums.PaymentMethod;
import lombok.Data;

@Data
public class PaymentDetails {

  private PaymentMethod paymentMethod;
  private String cardName;
  private String cardNumber;
  private String expiryMonth;
  private String expiryYear;
  private String cvv;
}
