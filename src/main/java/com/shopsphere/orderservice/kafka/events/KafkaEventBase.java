package com.shopsphere.orderservice.kafka.events;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class KafkaEventBase {

  private UUID eventId;
  private String eventType;
  private Instant initiatedAt;

  public KafkaEventBase(String eventType) {
    this.eventType = eventType;
    this.eventId = UUID.randomUUID();
    this.initiatedAt = Instant.now();
  }
}
