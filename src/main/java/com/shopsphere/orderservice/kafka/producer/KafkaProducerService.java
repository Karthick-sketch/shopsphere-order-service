package com.shopsphere.orderservice.kafka.producer;

import com.shopsphere.orderservice.kafka.events.OrderFailedEvent;
import com.shopsphere.orderservice.kafka.events.OrderPlacedEvent;
import com.shopsphere.orderservice.kafka.events.PaymentRequestEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

  private final String orderPlacedTopic;
  private final String orderFailedTopic;
  private final String paymentRequestTopic;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public KafkaProducerService(
    @Value("${kafka.topic.order-placed}") String orderPlacedTopic,
    @Value("${kafka.topic.order-failed}") String orderFailedTopic,
    @Value("${kafka.topic.payment-request}") String paymentRequestTopic,
    KafkaTemplate<String, Object> kafkaTemplate
  ) {
    this.orderPlacedTopic = orderPlacedTopic;
    this.orderFailedTopic = orderFailedTopic;
    this.paymentRequestTopic = paymentRequestTopic;
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendPaymentRequestEvent(PaymentRequestEvent event) {
    kafkaTemplate.send(paymentRequestTopic, event);
  }

  public void sendOrderPlacedEvent(OrderPlacedEvent event) {
    kafkaTemplate.send(orderPlacedTopic, event);
  }

  public void sendOrderFailedEvent(OrderFailedEvent event) {
    kafkaTemplate.send(orderFailedTopic, event);
  }
}
