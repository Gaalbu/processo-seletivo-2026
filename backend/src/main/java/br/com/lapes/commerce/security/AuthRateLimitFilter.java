package br.com.lapes.commerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
public class AuthRateLimitFilter extends OncePerRequestFilter {

  private final RateLimitService rateLimitService;
  private final RateLimitResponseWriter responseWriter;
  private final ClientIpResolver clientIpResolver;
  private final boolean enabled;
  private final int capacity;
  private final Duration refillWindow;

  public AuthRateLimitFilter(
      RateLimitService rateLimitService,
      RateLimitResponseWriter responseWriter,
      ClientIpResolver clientIpResolver,
      @Value("${app.rate-limit.enabled:true}") boolean enabled,
      @Value("${app.rate-limit.auth.capacity}") int capacity,
      @Value("${app.rate-limit.auth.refill-window-seconds}") long refillWindowSeconds) {
    this.rateLimitService = rateLimitService;
    this.responseWriter = responseWriter;
    this.clientIpResolver = clientIpResolver;
    this.enabled = enabled;
    this.capacity = capacity;
    this.refillWindow = Duration.ofSeconds(refillWindowSeconds);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (!enabled || !isAuthWrite(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String key = clientIpResolver.resolve(request) + ":" + request.getRequestURI();
    RateLimitResult result = rateLimitService.consume("auth", key, capacity, refillWindow);
    if (!result.allowed()) {
      responseWriter.write(request, response, result.retryAfterSeconds());
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean isAuthWrite(HttpServletRequest request) {
    return "POST".equals(request.getMethod())
        && ("/api/auth/login".equals(request.getRequestURI())
            || "/api/auth/register".equals(request.getRequestURI()));
  }

}
