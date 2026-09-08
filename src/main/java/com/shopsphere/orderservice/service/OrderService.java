package com.shopsphere.orderservice.service;

import com.shopsphere.orderservice.entity.Order;
import com.shopsphere.orderservice.entity.OrderItem;
import com.shopsphere.orderservice.enums.OrderStatus;
import com.shopsphere.orderservice.repository.OrderItemRepository;
import com.shopsphere.orderservice.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  public List<Order> findAll() {
    return orderRepository.findAll();
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

  public Order create(Order order) {
    return orderRepository.save(order);
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
}
