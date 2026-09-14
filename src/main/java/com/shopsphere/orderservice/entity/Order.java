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

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private OrderStatus status = OrderStatus.PENDING;

  @Column(name = "placed_at", nullable = false)
  @Builder.Default
  private LocalDateTime placedAt = LocalDateTime.now();

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal subTotal;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal shipping;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal total;

  @Column(nullable = false)
  private String shippingName;

  @Column(nullable = false)
  private String shippingAddress;

  @Column(nullable = false)
  private String cardLast4;

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
