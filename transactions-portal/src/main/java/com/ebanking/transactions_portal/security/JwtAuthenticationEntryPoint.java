package com.ebanking.transactions_portal.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) throws IOException {

    System.out.println("Responding with unauthorized error. Message - {}" + authException.getMessage());

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now().toString());
    errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    errorResponse.put("error", "Unauthorized");
    errorResponse.put("message", "Access denied. Please provide a valid JWT token.");
    errorResponse.put("path", request.getRequestURI());

    if (authException.getMessage() != null) {
      errorResponse.put("details", authException.getMessage());
    }

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
