package com.shopsphere.orderservice.exceptions;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleOrderNotFoundException(
    OrderNotFoundException e
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      errorBody(e.getMessage())
    );
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<Map<String, String>> handleInsufficientStockException(
    InsufficientStockException e
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
      errorBody(e.getMessage())
    );
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, String>> handleRuntimeException(
    RuntimeException e
  ) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      errorBody(e.getMessage())
    );
  }

  private Map<String, String> errorBody(String message) {
    return java.util.Map.of("error", message);
  }
}
