package com.shopsphere.orderservice.entity;

import com.shopsphere.orderservice.enums.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private OrderStatus status = OrderStatus.PENDING;

  @Column(name = "order_date", nullable = false)
  @Builder.Default
  private LocalDateTime orderDate = LocalDateTime.now();

  @OneToMany(
    mappedBy = "order",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @Builder.Default
  private List<OrderItem> items = new ArrayList<>();

  @Column(nullable = false)
  private Long userId;
}
