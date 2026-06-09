package br.com.lapes.commerce.security;

public record RateLimitResult(boolean allowed, long retryAfterSeconds) {

  public static RateLimitResult accepted() {
    return new RateLimitResult(true, 0);
  }

  public static RateLimitResult rejected(long retryAfterSeconds) {
    return new RateLimitResult(false, Math.max(1, retryAfterSeconds));
  }
}
