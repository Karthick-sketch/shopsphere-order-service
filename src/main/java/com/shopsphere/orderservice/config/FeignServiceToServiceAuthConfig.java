package com.shopsphere.orderservice.config;

import com.shopsphere.orderservice.dto.auth.ServiceTokenResponse;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * Feign client configuration for service-to-service authentication.
 * Obtains a JWT from the auth service using client credentials grant,
 * caches the token until near-expiry, and attaches it to outbound requests.
 */
@Configuration
public class FeignServiceToServiceAuthConfig {

  private static final Logger log = LoggerFactory.getLogger(
    FeignServiceToServiceAuthConfig.class
  );
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private static final long EXPIRY_BUFFER_SECONDS = 60;

  @Value("${service.auth.client-id}")
  private String clientId;

  @Value("${service.auth.client-secret}")
  private String clientSecret;

  @Value("${service.auth.token-uri}")
  private String tokenUri;

  private String cachedToken;
  private Instant tokenExpiry = Instant.EPOCH;

  @Bean
  public RestTemplate serviceAuthRestTemplate() {
    return new RestTemplate();
  }

  @Bean
  public RequestInterceptor serviceAuthInterceptor(
    RestTemplate serviceAuthRestTemplate
  ) {
    return (RequestTemplate template) -> {
      String token = getToken(serviceAuthRestTemplate);
      if (token != null) {
        template.header(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
      }
    };
  }

  private synchronized String getToken(RestTemplate restTemplate) {
    if (
      cachedToken != null &&
      Instant.now().isBefore(tokenExpiry.minusSeconds(EXPIRY_BUFFER_SECONDS))
    ) {
      return cachedToken;
    }

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      Map<String, String> body = Map.of(
        "clientId",
        clientId,
        "clientSecret",
        clientSecret
      );

      HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

      ResponseEntity<ServiceTokenResponse> response =
        restTemplate.postForEntity(
          tokenUri,
          request,
          ServiceTokenResponse.class
        );

      if (
        response.getStatusCode().is2xxSuccessful() && response.getBody() != null
      ) {
        ServiceTokenResponse responseBody = response.getBody();
        cachedToken = responseBody.getAccessToken();
        long expiresIn = responseBody.getExpiresIn();
        tokenExpiry = Instant.now().plusSeconds(expiresIn);
        log.debug("Obtained new service token, expires in {}s", expiresIn);
        return cachedToken;
      }
    } catch (Exception e) {
      log.error("Failed to obtain service token from auth service", e);
    }

    return cachedToken;
  }
}
