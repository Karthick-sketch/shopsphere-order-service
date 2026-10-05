package com.shopsphere.orderservice.kafka.consumer;

import com.shopsphere.orderservice.kafka.events.PaymentResponseEvent;
import com.shopsphere.orderservice.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaConsumerService {

  private final OrderService orderService;

  @KafkaListener(
    topics = "${kafka.topic.payment-response}",
    groupId = "${kafka.consumer.group-id}"
  )
  public void handlePaymentResponseEvent(PaymentResponseEvent event) {
    orderService.handlePaymentResponse(event.getData());
  }
}
