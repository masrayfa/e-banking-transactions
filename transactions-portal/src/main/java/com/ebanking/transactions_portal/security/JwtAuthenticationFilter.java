package com.ebanking.transactions_portal.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestURI = request.getRequestURI();
    String method = request.getMethod();

    System.out.println("=== JWT FILTER DEBUG ===");
    System.out.println("Processing: " + method + " " + requestURI);
    System.out.println("Current thread: " + Thread.currentThread().getName());

    try {
      String jwt = getJwtFromRequest(request);
      System.out.println("JWT Token extracted: " + (jwt != null ? "YES (length: " + jwt.length() + ")" : "NO"));

      if (StringUtils.hasText(jwt)) {
        boolean isValid = jwtTokenProvider.validateToken(jwt);
        System.out.println("Token validation result: " + isValid);

        if (isValid) {
          String customerId = jwtTokenProvider.getCustomerIdFromToken(jwt);
          List<String> roles = jwtTokenProvider.getRolesFromToken(jwt);

          System.out.println("Customer ID from token: " + customerId);
          System.out.println("Roles from token: " + roles);

          // Convert roles to authorities
          List<SimpleGrantedAuthority> authorities = roles.stream()
              .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
              .toList();

          System.out.println("Spring Security Authorities: " + authorities);

          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customerId, null,
              authorities);
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authentication);

          System.out.println("Authentication set in SecurityContext: " + authentication.isAuthenticated());
          System.out.println("Principal: " + authentication.getName());
          System.out.println("Authorities in auth: " + authentication.getAuthorities());

        } else {
          System.out.println("Token validation failed");
        }
      } else {
        System.out.println("No JWT token found in request");
      }
    } catch (Exception ex) {
      System.err.println("JWT Filter Exception: " + ex.getMessage());
      ex.printStackTrace();
      SecurityContextHolder.clearContext();
    }

    System.out.println("Final SecurityContext Authentication: " +
        (SecurityContextHolder.getContext().getAuthentication() != null
            ? SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
            : "NULL"));
    System.out.println("========================");

    filterChain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    System.out.println("Authorization header: " + bearerToken);

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      String token = bearerToken.substring(7); // Remove "Bearer " prefix
      System.out.println("Extracted token (first 20 chars): " +
          (token.length() > 20 ? token.substring(0, 20) + "..." : token));
      return token;
    }
    return null;
  }

  /**
   * Skip JWT for public endpoints
   */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    boolean shouldSkip = path.startsWith("/api/public/") ||
        path.startsWith("/health/database") ||
        path.startsWith("/actuator/health") ||
        path.startsWith("/swagger-ui/") ||
        path.startsWith("/v3/api-docs") ||
        path.startsWith("/api/test/generate-token") ||
        path.startsWith("/api/test/validate-token") ||
        path.equals("/favicon.ico");

    if (shouldSkip) {
      System.out.println("Skipping JWT filter for public endpoint: " + path);
    }

    return shouldSkip;
  }
}
