package com.shopsphere.orderservice.service;

import com.shopsphere.orderservice.dto.*;
import com.shopsphere.orderservice.dto.payment.*;
import com.shopsphere.orderservice.dto.product.*;
import com.shopsphere.orderservice.entity.*;
import com.shopsphere.orderservice.enums.*;
import com.shopsphere.orderservice.feign.*;
import com.shopsphere.orderservice.kafka.KafkaProducerService;
import com.shopsphere.orderservice.kafka.OrderPlacedEvent;
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
  private final PaymentInterface paymentInterface;

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

    PaymentResponse response = processPayment(
      order,
      orderRequest.getPaymentDetails()
    );
    if (PaymentStatus.FAILED.equals(response.getStatus())) {
      order.setStatus(OrderStatus.PAYMENT_FAILED);
      order = orderRepository.save(order);
      throw new RuntimeException("Payment failed");
    }

    kafkaProducerService.sendOrderPlacedEvent(toOrderPlacedEvent(order));

    return toOrderResponse(order);
  }

  private PaymentResponse processPayment(Order order, PaymentDetails details) {
    PaymentRequest paymentRequest = toPaymentRequest(
      order.getId(),
      order.getTotal(),
      details
    );
    return paymentInterface.processPayment(paymentRequest).getBody();
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
        PaymentMethod.CARD.equals(
          request.getPaymentDetails().getPaymentMethod()
        )
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

  private PaymentRequest toPaymentRequest(
    Long orderId,
    BigDecimal amount,
    PaymentDetails details
  ) {
    return PaymentRequest.builder()
      .orderId(orderId)
      .amount(amount)
      .paymentMethod(details.getPaymentMethod())
      .cardName(details.getCardName())
      .cardNumber(details.getCardNumber())
      .expiryMonth(details.getExpiryMonth())
      .expiryYear(details.getExpiryYear())
      .cvv(details.getCvv())
      .build();
  }
}
