package br.com.lapes.commerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI(@Value("${server.port:8080}") int port) {
    return new OpenAPI()
        .servers(List.of(
            new Server().url("http://localhost:" + port).description("Local development"),
            new Server().url("/").description("Default")))
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .info(
            new Info()
                .title("LAPES Commerce API")
                .version("0.2.0")
                .description("API do e-commerce simplificado do Processo Seletivo LAPES 2026.")
                .contact(new Contact().name("LAPES").email("contato@lapes.com")));
  }
}
