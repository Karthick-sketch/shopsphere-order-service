package com.shopsphere.orderservice.exceptions;

public class OrderNotFoundException extends RuntimeException {

  public OrderNotFoundException(Long id) {
    super("Order not found with id: " + id);
  }
}
