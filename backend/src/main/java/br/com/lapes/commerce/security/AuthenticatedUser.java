package br.com.lapes.commerce.security;

import br.com.lapes.commerce.domain.User;
import br.com.lapes.commerce.domain.UserRole;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticatedUser implements UserDetails {

  private final UUID id;
  private final String name;
  private final String email;
  private final String passwordHash;
  private final UserRole role;

  public AuthenticatedUser(User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.email = user.getEmail();
    this.passwordHash = user.getPasswordHash();
    this.role = user.getRole();
  }

  public UUID id() {
    return id;
  }

  public String name() {
    return name;
  }

  public UserRole role() {
    return role;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getPassword() {
    return passwordHash;
  }

  @Override
  public String getUsername() {
    return email;
  }
}
