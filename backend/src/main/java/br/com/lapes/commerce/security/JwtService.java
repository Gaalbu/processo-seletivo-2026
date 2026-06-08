package br.com.lapes.commerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey key;
  private final Duration expiration;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expiration = Duration.ofMinutes(expirationMinutes);
  }

  public String generateToken(AuthenticatedUser user) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(expiration);

    return Jwts.builder()
        .subject(user.getUsername())
        .claim("userId", user.id().toString())
        .claim("role", user.role().name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiresAt))
        .signWith(key)
        .compact();
  }

  public String extractSubject(String token) {
    return claims(token).getSubject();
  }

  public boolean isValid(String token, AuthenticatedUser user) {
    return user.getUsername().equals(extractSubject(token)) && claims(token).getExpiration().after(new Date());
  }

  private Claims claims(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }
}
