package br.com.lapes.commerce.auth;

import br.com.lapes.commerce.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Authentication and account management")
public class AuthController {

  private final AuthService authService;
  private final TokenBlacklist tokenBlacklist;

  public AuthController(AuthService authService, TokenBlacklist tokenBlacklist) {
    this.authService = authService;
    this.tokenBlacklist = tokenBlacklist;
  }

  @Operation(summary = "Register a new user")
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return authService.register(request);
  }

  @Operation(summary = "Authenticate and get JWT token")
  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request);
  }

  @Operation(summary = "Invalidate current JWT token")
  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @SecurityRequirement(name = "bearerAuth")
  public void logout(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    if (authorization != null && authorization.startsWith("Bearer ")) {
      String token = authorization.substring(7);
      tokenBlacklist.invalidate(token, Duration.ofHours(2));
    }
  }

  @Operation(summary = "Get current authenticated user")
  @GetMapping("/me")
  @SecurityRequirement(name = "bearerAuth")
  public AuthResponse.UserResponse me(@AuthenticationPrincipal AuthenticatedUser user) {
    return new AuthResponse.UserResponse(user.id(), user.name(), user.getUsername(), user.role());
  }
}
