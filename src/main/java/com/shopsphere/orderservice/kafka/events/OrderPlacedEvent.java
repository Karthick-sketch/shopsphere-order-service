package com.shopsphere.orderservice.kafka.events;

import com.shopsphere.orderservice.dto.OrderPlacedData;
import com.shopsphere.orderservice.kafka.KafkaConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderPlacedEvent extends KafkaEventBase {

  private OrderPlacedData data;

  public OrderPlacedEvent(OrderPlacedData data) {
    super(KafkaConstants.ORDER_PLACED_EVENT_TYPE);
    this.data = data;
  }
}
