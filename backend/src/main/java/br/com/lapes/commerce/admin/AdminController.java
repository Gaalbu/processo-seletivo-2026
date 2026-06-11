package br.com.lapes.commerce.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "Administrative operations")
public class AdminController {

  @Operation(summary = "Admin health check")
  @GetMapping("/ping")
  @PreAuthorize("hasRole('ADMIN')")
  public Map<String, String> ping() {
    return Map.of("status", "admin-ok");
  }
}
