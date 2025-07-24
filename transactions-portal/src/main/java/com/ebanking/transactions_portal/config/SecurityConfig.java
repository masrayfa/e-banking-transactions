package com.ebanking.transactions_portal.config;

import com.ebanking.transactions_portal.security.JwtAuthenticationEntryPoint;
import com.ebanking.transactions_portal.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  @Autowired
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    System.out.println("=== SECURITY CONFIG DEBUG ===");
    System.out.println("Configuring security filter chain...");

    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authz -> {
          System.out.println("Configuring authorization rules...");
          authz
              // Public endpoints
              .requestMatchers("/api/public/**").permitAll()
              .requestMatchers("/actuator/health/**").permitAll()
              .requestMatchers("/health/database/**").permitAll()
              .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
              .requestMatchers("/favicon.ico").permitAll()

              .requestMatchers("/api/test/**").permitAll()

              // Protected endpoints - DEBUG LOG
              .requestMatchers("/api/v1/transactions/**").access((auth, context) -> {
                System.out.println("=== AUTHORIZATION CHECK ===");
                System.out.println("Request URL: " + context.getRequest().getRequestURI());
                System.out.println("Authentication: " + auth.get());
                System.out.println("Is Authenticated: " + (auth.get() != null ? auth.get().isAuthenticated() : "null"));
                if (auth.get() != null && auth.get().getAuthorities() != null) {
                  System.out.println("Authorities: " + auth.get().getAuthorities());
                  boolean hasCustomerRole = auth.get().getAuthorities().stream()
                      .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
                  System.out.println("Has ROLE_CUSTOMER: " + hasCustomerRole);
                  System.out.println("Authorization result: " + hasCustomerRole);
                  System.out.println("===============================");
                  return new org.springframework.security.authorization.AuthorizationDecision(hasCustomerRole);
                } else {
                  System.out.println("No authentication or authorities found");
                  System.out.println("Authorization result: DENY");
                  System.out.println("===============================");
                  return new org.springframework.security.authorization.AuthorizationDecision(false);
                }
              })
              .anyRequest().authenticated();
        });

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    System.out.println("Security filter chain configured successfully");
    System.out.println("================================");

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
