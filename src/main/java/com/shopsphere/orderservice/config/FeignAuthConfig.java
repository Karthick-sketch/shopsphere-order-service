package com.shopsphere.orderservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign client configuration that propagates the caller's JWT token
 * to downstream services. Extracts the Authorization header from the
 * current HTTP request and forwards it on the outgoing Feign call.
 */
@Configuration
public class FeignAuthConfig {

  private static final String AUTHORIZATION_HEADER = "Authorization";

  @Bean
  public RequestInterceptor jwtRelayInterceptor() {
    return (RequestTemplate template) -> {
      ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

      if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && !authHeader.isBlank()) {
          template.header(AUTHORIZATION_HEADER, authHeader);
        }
      }
    };
  }
}
