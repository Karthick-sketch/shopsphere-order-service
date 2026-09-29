package com.shopsphere.orderservice.repository;

import com.shopsphere.orderservice.entity.Order;
import com.shopsphere.orderservice.enums.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByAuthUserId(Long authUserId);

  List<Order> findByStatus(OrderStatus status);

  List<Order> findByAuthUserIdAndStatus(Long authUserId, OrderStatus status);
}
