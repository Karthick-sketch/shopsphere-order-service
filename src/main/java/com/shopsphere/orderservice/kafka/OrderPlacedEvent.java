package com.shopsphere.orderservice.kafka;

import com.shopsphere.orderservice.dto.OrderPlacedData;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderPlacedEvent {

  private UUID eventId;
  private String eventType;
  private Instant initiatedAt;
  private OrderPlacedData data;

  public OrderPlacedEvent(OrderPlacedData data) {
    this.data = data;
    this.eventId = UUID.randomUUID();
    this.initiatedAt = Instant.now();
    this.eventType = KafkaConstants.ORDER_PLACED_EVENT_TYPE;
  }
}
