package com.shopsphere.orderservice.service;

import com.shopsphere.orderservice.dto.*;
import com.shopsphere.orderservice.dto.product.ProductIdsRequest;
import com.shopsphere.orderservice.dto.product.ProductSummary;
import com.shopsphere.orderservice.entity.*;
import com.shopsphere.orderservice.enums.OrderStatus;
import com.shopsphere.orderservice.feign.ProductInterface;
import com.shopsphere.orderservice.repository.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  private final ProductInterface productInterface;

  public List<OrderResponse> findAll() {
    return orderRepository
      .findAll()
      .stream()
      .map(this::toOrderResponse)
      .toList();
  }

  public Order findById(Long id) {
    return orderRepository
      .findById(id)
      .orElseThrow(() ->
        new RuntimeException("Order not found with id: " + id)
      );
  }

  public List<Order> findByUserId(Long userId) {
    return orderRepository.findByUserId(userId);
  }

  public List<Order> findByStatus(OrderStatus status) {
    return orderRepository.findByStatus(status);
  }

  public OrderResponse create(OrderRequest orderRequest) {
    Order order = toOrder(orderRequest);
    List<OrderItem> items = orderRequest
      .getOrderItems()
      .stream()
      .map(item -> toOrderItem(item, order))
      .toList();
    order.setItems(items);
    return toOrderResponse(orderRepository.save(order));
  }

  public Order updateStatus(Long id, OrderStatus status) {
    Order existing = findById(id);
    existing.setStatus(status);
    return orderRepository.save(existing);
  }

  public void delete(Long id) {
    findById(id);
    orderRepository.deleteById(id);
  }

  public List<OrderItem> findItemsByOrderId(Long orderId) {
    return orderItemRepository.findByOrderId(orderId);
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

  private Order toOrder(OrderRequest order) {
    return Order.builder()
      .status(order.getStatus())
      .placedAt(order.getPlacedAt())
      .subtotal(order.getSubtotal())
      .shipping(order.getShipping())
      .total(order.getTotal())
      .shippingName(order.getShippingName())
      .shippingAddress(order.getShippingAddress())
      .cardLast4(order.getCardLast4())
      .userId(order.getUserId())
      .build();
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
      .userId(order.getUserId())
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
}
