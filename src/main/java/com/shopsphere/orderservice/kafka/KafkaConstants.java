package com.shopsphere.orderservice.kafka;

public final class KafkaConstants {

  private KafkaConstants() {}

  // consumer configs
  public static final String AUTO_OFFSET_RESET_EARLIEST = "earliest";
  public static final String TRUST_ALL_PACKAGES = "*";

  // event types
  public static final String ORDER_PLACED_EVENT_TYPE = "ORDER_PLACED";
  public static final String PAYMENT_REQUEST_EVENT_TYPE = "PAYMENT_REQUEST";
  public static final String PAYMENT_RESPONSE_EVENT_TYPE = "PAYMENT_RESPONSE";
}
