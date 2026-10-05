package com.shopsphere.orderservice.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

  private final String topic;
  private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

  public KafkaProducerService(
    @Value("${kafka.topic.order-placed}") String topic,
    KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate
  ) {
    this.topic = topic;
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendOrderPlacedEvent(OrderPlacedEvent event) {
    kafkaTemplate.send(topic, event);
  }
}
