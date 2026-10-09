package com.shopsphere.orderservice.controller;

import com.shopsphere.orderservice.dto.OrderRequest;
import com.shopsphere.orderservice.dto.OrderResponse;
import com.shopsphere.orderservice.dto.OrderStatusResponse;
import com.shopsphere.orderservice.entity.Order;
import com.shopsphere.orderservice.entity.OrderItem;
import com.shopsphere.orderservice.service.OrderService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @GetMapping
  public ResponseEntity<List<OrderResponse>> getAll(
    @AuthenticationPrincipal Jwt jwt
  ) {
    Long authUserId = Long.valueOf(jwt.getClaim("sub"));
    return ResponseEntity.ok(orderService.findByAuthUserId(authUserId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Order> getById(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.findById(id));
  }

  @GetMapping("/{id}/items")
  public ResponseEntity<List<OrderItem>> getItems(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.findItemsByOrderId(id));
  }

  @GetMapping("/{id}/status")
  public CompletableFuture<ResponseEntity<OrderStatusResponse>> getStatus(
    @PathVariable Long id
  ) {
    return orderService
      .waitForPayment(id)
      .thenApply(res -> ResponseEntity.ok(res));
  }

  @PostMapping
  public ResponseEntity<OrderResponse> checkout(
    @RequestBody OrderRequest orderRequest,
    @AuthenticationPrincipal Jwt jwt
  ) {
    Long authUserId = Long.valueOf(jwt.getClaim("sub"));
    return ResponseEntity.status(HttpStatus.CREATED).body(
      orderService.create(authUserId, orderRequest)
    );
  }
}
