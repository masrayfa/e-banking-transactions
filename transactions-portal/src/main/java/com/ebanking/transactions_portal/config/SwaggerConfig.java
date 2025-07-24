package com.ebanking.transactions_portal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

  @Value("${server.port:8080}")
  private String serverPort;

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("E-Banking Transactions Portal API")
            .description(
                "REST API for retrieving customer transaction data with currency conversion capabilities")
            .version("1.0.0")
            .contact(new Contact()
                .name("E-Banking Team")
                .email("ebanking-team@company.com")
                .url("https://company.com/ebanking"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT")))
        .servers(List.of(
            new Server()
                .url("http://localhost:" + serverPort)
                .description("Development Server"),
            new Server()
                .url("https://api.ebanking.company.com")
                .description("Production Server")))
        .tags(List.of(
            new Tag()
                .name("Transaction API")
                .description(
                    "Operations for retrieving customer transactions with currency conversion and pagination support")));
  }
}
