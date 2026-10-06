package com.shopsphere.orderservice.service;

import com.shopsphere.orderservice.dto.*;
import com.shopsphere.orderservice.dto.payment.*;
import com.shopsphere.orderservice.dto.product.*;
import com.shopsphere.orderservice.entity.*;
import com.shopsphere.orderservice.enums.*;
import com.shopsphere.orderservice.feign.*;
import com.shopsphere.orderservice.kafka.events.OrderPlacedEvent;
import com.shopsphere.orderservice.kafka.events.PaymentRequestEvent;
import com.shopsphere.orderservice.kafka.producer.KafkaProducerService;
import com.shopsphere.orderservice.repository.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  private final KafkaProducerService kafkaProducerService;

  private final ProductInterface productInterface;

  public Order findById(Long id) {
    return orderRepository
      .findById(id)
      .orElseThrow(() ->
        new RuntimeException("Order not found with id: " + id)
      );
  }

  public List<OrderResponse> findByAuthUserId(Long authUserId) {
    return orderRepository
      .findByAuthUserId(authUserId)
      .stream()
      .map(this::toOrderResponse)
      .toList();
  }

  public List<OrderItem> findItemsByOrderId(Long orderId) {
    return orderItemRepository.findByOrderId(orderId);
  }

  @Transactional
  public OrderResponse create(Long authUserId, OrderRequest orderRequest) {
    Order order = orderRepository.save(toOrder(orderRequest, authUserId));

    if (PaymentMethod.CARD.equals(orderRequest.getPaymentMethod())) {
      PaymentRequestEvent event = toPaymentRequestEvent(
        order.getId(),
        order.getTotal(),
        orderRequest.getPaymentMethod(),
        orderRequest.getPaymentToken()
      );
      kafkaProducerService.sendPaymentRequestEvent(event);
    } else if (PaymentMethod.COD.equals(orderRequest.getPaymentMethod())) {
      sendOrderPlacedEvent(order);
    } else {
      throw new RuntimeException("Invalid payment method");
    }

    return toOrderResponse(order);
  }

  @Transactional
  public void handlePaymentResponse(PaymentResponseData data) {
    Order order = findById(data.getOrderId());
    if (data.getStatus().equals(PaymentStatus.SUCCESS)) {
      order.setStatus(OrderStatus.CONFIRMED);
      sendOrderPlacedEvent(order);
    } else {
      order.setStatus(OrderStatus.PAYMENT_FAILED);
    }
    orderRepository.save(order);
  }

  private void sendOrderPlacedEvent(Order order) {
    kafkaProducerService.sendOrderPlacedEvent(toOrderPlacedEvent(order));
  }

  private OrderPlacedEvent toOrderPlacedEvent(Order order) {
    return new OrderPlacedEvent(
      new OrderPlacedData(
        order.getAuthUserId(),
        order.getId(),
        order.getItems().stream().map(this::toOrderItemData).toList()
      )
    );
  }

  private OrderItemData toOrderItemData(OrderItem item) {
    return new OrderItemData(item.getProductId(), item.getQuantity());
  }

  private List<ProductSummary> getProductSummaries(List<Long> productIds) {
    return productInterface
      .getSummary(new ProductIdsRequest(productIds))
      .getBody();
  }

  private List<Long> getProductIds(List<OrderItem> items) {
    return items
      .stream()
      .map(item -> item.getProductId())
      .distinct()
      .toList();
  }

  private Order toOrder(OrderRequest request, Long authUserId) {
    Order order = Order.builder()
      .subtotal(request.getSubtotal())
      .shipping(request.getShipping())
      .total(request.getTotal())
      .shippingName(request.getShippingName())
      .shippingAddress(request.getShippingAddress())
      .cardLast4(request.getCardLast4())
      .authUserId(authUserId)
      .placedAt(LocalDateTime.now())
      .status(
        PaymentMethod.CARD.equals(request.getPaymentMethod())
          ? OrderStatus.PAYMENT_PENDING
          : OrderStatus.CONFIRMED
      )
      .build();

    List<OrderItem> items = request
      .getOrderItems()
      .stream()
      .map(item -> toOrderItem(item, order))
      .toList();
    order.setItems(items);

    return order;
  }

  private OrderItem toOrderItem(OrderItemRequest orderItem, Order order) {
    return OrderItem.builder()
      .order(order)
      .quantity(orderItem.getQuantity())
      .price(orderItem.getPrice())
      .productId(orderItem.getProductId())
      .build();
  }

  private OrderResponse toOrderResponse(Order order) {
    List<ProductSummary> productSummaries = getProductSummaries(
      getProductIds(order.getItems())
    );

    return OrderResponse.builder()
      .id(order.getId())
      .status(order.getStatus())
      .placedAt(order.getPlacedAt())
      .subtotal(order.getSubtotal())
      .shipping(order.getShipping())
      .total(order.getTotal())
      .shippingName(order.getShippingName())
      .shippingAddress(order.getShippingAddress())
      .cardLast4(order.getCardLast4())
      .authUserId(order.getAuthUserId())
      .items(
        order
          .getItems()
          .stream()
          .map(item -> toOrderItemResponse(item, productSummaries))
          .toList()
      )
      .build();
  }

  private OrderItemResponse toOrderItemResponse(
    OrderItem item,
    List<ProductSummary> productSummaries
  ) {
    return OrderItemResponse.builder()
      .id(item.getId())
      .orderId(item.getOrder().getId())
      .quantity(item.getQuantity())
      .price(item.getPrice())
      .productSummary(
        productSummaries
          .stream()
          .filter(p -> p.getId().equals(item.getProductId()))
          .findFirst()
          .orElse(null)
      )
      .build();
  }

  private PaymentRequestEvent toPaymentRequestEvent(
    Long orderId,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    String paymentToken
  ) {
    return new PaymentRequestEvent(
      new PaymentRequestData(orderId, amount, paymentMethod, paymentToken)
    );
  }
}
