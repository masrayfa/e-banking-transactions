package com.ebanking.transactions_portal.controller;

import com.ebanking.transactions_portal.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Token Test", description = "Endpoint sementara untuk generate token (HANYA UNTUK TESTING)")
public class TokenTestController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/generate-token")
    @Operation(summary = "Generate JWT Token", description = "Endpoint sementara untuk generate JWT token dengan customerId dan roles")
    public ResponseEntity<Map<String, Object>> generateToken(
            @Parameter(description = "ID Customer", example = "customer123") @RequestParam String customerId,
            @Parameter(description = "Roles (comma separated)", example = "USER,CUSTOMER") @RequestParam(defaultValue = "CUSTOMER") String roles) {

        try {
            // Convert comma-separated roles to List
            List<String> rolesList = Arrays.asList(roles.split(","));

            String token = jwtTokenProvider.generateToken(customerId, rolesList);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customerId", customerId);
            response.put("roles", rolesList);
            response.put("token", token);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", "24 hours");

            System.out.println("=== TOKEN GENERATED ===");
            System.out.println("Customer ID: " + customerId);
            System.out.println("Roles: " + rolesList);
            System.out.println("Token: " + token);
            System.out.println("======================");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to generate token");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/generate-token-simple")
    @Operation(summary = "Generate Simple JWT Token", description = "Generate token dengan parameter default untuk testing cepat")
    public ResponseEntity<Map<String, Object>> generateSimpleToken() {

        String customerId = "testCustomer";
        List<String> roles = Arrays.asList("USER", "CUSTOMER");

        try {
            String token = jwtTokenProvider.generateToken(customerId, roles);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customerId", customerId);
            response.put("roles", roles);
            response.put("token", token);
            response.put("tokenType", "Bearer");
            response.put("fullAuthHeader", "Bearer " + token);

            System.out.println("=== SIMPLE TOKEN GENERATED ===");
            System.out.println("Full Authorization Header: Bearer " + token);
            System.out.println("==============================");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to generate simple token");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/validate-token")
    @Operation(summary = "Validate JWT Token", description = "Validate token dan extract informasi dari dalamnya")
    public ResponseEntity<Map<String, Object>> validateToken(
            @Parameter(description = "JWT Token (tanpa 'Bearer ')", example = "eyJhbGciOiJIUzI1NiJ9...") @RequestParam String token) {

        try {
            boolean isValid = jwtTokenProvider.validateToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);

            if (isValid) {
                String customerId = jwtTokenProvider.getCustomerIdFromToken(token);
                List<String> roles = jwtTokenProvider.getRolesFromToken(token);

                response.put("customerId", customerId);
                response.put("roles", roles);
                response.put("message", "Token is valid");
            } else {
                response.put("message", "Token is invalid or expired");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("error", "Failed to validate token");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/debug-token")
    public ResponseEntity<Map<String, Object>> debugToken(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean isValid = jwtTokenProvider.validateToken(token);
            response.put("isValid", isValid);

            if (isValid) {
                String customerId = jwtTokenProvider.getCustomerIdFromToken(token);
                List<String> roles = jwtTokenProvider.getRolesFromToken(token);

                response.put("customerId", customerId);
                response.put("roles", roles);
                response.put("hasCustomerRole", roles.contains("CUSTOMER"));
                response.put("tokenLength", token.length());
            } else {
                response.put("error", "Token is not valid");
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("exception", e.getClass().getSimpleName());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth-test")
    @Operation(summary = "Test Authenticated Endpoint", description = "Endpoint untuk test autentikasi JWT (memerlukan token)")
    public ResponseEntity<Map<String, Object>> testAuthentication(
            @Parameter(hidden = true) @org.springframework.security.core.annotation.AuthenticationPrincipal Object principal,
            @Parameter(hidden = true) org.springframework.security.core.Authentication authentication) {

        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Authentication successful!");

            if (authentication != null) {
                response.put("authenticated", authentication.isAuthenticated());
                response.put("principal", authentication.getName());
                response.put("authorities", authentication.getAuthorities().toString());
            } else {
                response.put("error", "Authentication object is null");
            }

            System.out.println("=== AUTH TEST ENDPOINT ===");
            System.out.println("Principal: " + (authentication != null ? authentication.getName() : "null"));
            System.out.println("Authorities: " + (authentication != null ? authentication.getAuthorities() : "null"));
            System.out.println("=========================");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Authentication test failed");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
