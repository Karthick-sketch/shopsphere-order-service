package com.shopsphere.orderservice.kafka.consumer;

import com.shopsphere.orderservice.kafka.KafkaConstants;
import com.shopsphere.orderservice.kafka.events.PaymentResponseEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

@Configuration
public class KafkaConsumerConfig {

  @Value("${kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${kafka.consumer.group-id}")
  private String groupId;

  @Bean
  public ConsumerFactory<String, PaymentResponseEvent> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
      KafkaConstants.AUTO_OFFSET_RESET_EARLIEST
    );
    props.put(
      ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS,
      StringDeserializer.class
    );
    props.put(
      ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
      JacksonJsonDeserializer.class
    );

    JacksonJsonDeserializer<PaymentResponseEvent> valueDeserializer =
      new JacksonJsonDeserializer<>(PaymentResponseEvent.class);
    valueDeserializer.addTrustedPackages(KafkaConstants.TRUST_ALL_PACKAGES);
    valueDeserializer.setUseTypeHeaders(false);

    return new DefaultKafkaConsumerFactory<>(
      props,
      new ErrorHandlingDeserializer<>(new StringDeserializer()),
      new ErrorHandlingDeserializer<>(valueDeserializer)
    );
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<
    String,
    PaymentResponseEvent
  > kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<
      String,
      PaymentResponseEvent
    > factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }
}
