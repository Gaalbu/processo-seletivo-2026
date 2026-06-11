package br.com.lapes.commerce.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Application health check")
public class HealthController {

  @Operation(summary = "Health check endpoint")
  @GetMapping
  public HealthResponse health() {
    return new HealthResponse("ok", Instant.now());
  }

  public record HealthResponse(String status, Instant timestamp) {}
}
