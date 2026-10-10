package com.shopsphere.orderservice.kafka.events;

import com.shopsphere.orderservice.dto.OrderEventData;
import com.shopsphere.orderservice.kafka.KafkaConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderFailedEvent extends KafkaEventBase {

  private OrderEventData data;

  public OrderFailedEvent(OrderEventData data) {
    super(KafkaConstants.ORDER_FAILED_EVENT_TYPE);
    this.data = data;
  }
}
