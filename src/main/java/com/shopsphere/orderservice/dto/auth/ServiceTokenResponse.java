package com.shopsphere.orderservice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceTokenResponse {

  private String accessToken;
  private long expiresIn;
  private String tokenType;
}
