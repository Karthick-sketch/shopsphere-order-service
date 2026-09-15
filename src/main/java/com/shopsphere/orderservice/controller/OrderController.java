package com.shopsphere.orderservice.controller;

import com.shopsphere.orderservice.dto.OrderRequest;
import com.shopsphere.orderservice.dto.OrderResponse;
import com.shopsphere.orderservice.entity.Order;
import com.shopsphere.orderservice.entity.OrderItem;
import com.shopsphere.orderservice.enums.OrderStatus;
import com.shopsphere.orderservice.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @GetMapping
  public ResponseEntity<List<OrderResponse>> getAll() {
    return ResponseEntity.ok(orderService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Order> getById(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.findById(id));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<Order>> getByUserId(@PathVariable Long userId) {
    return ResponseEntity.ok(orderService.findByUserId(userId));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<Order>> getByStatus(
    @PathVariable OrderStatus status
  ) {
    return ResponseEntity.ok(orderService.findByStatus(status));
  }

  @GetMapping("/{id}/items")
  public ResponseEntity<List<OrderItem>> getItems(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.findItemsByOrderId(id));
  }

  @PostMapping
  public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest order) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
      orderService.create(order)
    );
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<Order> updateStatus(
    @PathVariable Long id,
    @RequestParam OrderStatus status
  ) {
    return ResponseEntity.ok(orderService.updateStatus(id, status));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    orderService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
