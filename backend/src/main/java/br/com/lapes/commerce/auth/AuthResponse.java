package br.com.lapes.commerce.auth;

import br.com.lapes.commerce.domain.UserRole;
import java.util.UUID;

public record AuthResponse(String token, UserResponse user) {

  public record UserResponse(UUID id, String name, String email, UserRole role) {}
}
