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
@Order(2)
public class CatalogRateLimitFilter extends OncePerRequestFilter {

  private final RateLimitService rateLimitService;
  private final RateLimitResponseWriter responseWriter;
  private final ClientIpResolver clientIpResolver;
  private final boolean enabled;
  private final int capacity;
  private final Duration refillWindow;

  public CatalogRateLimitFilter(
      RateLimitService rateLimitService,
      RateLimitResponseWriter responseWriter,
      ClientIpResolver clientIpResolver,
      @Value("${app.rate-limit.enabled:true}") boolean enabled,
      @Value("${app.rate-limit.catalog.capacity}") int capacity,
      @Value("${app.rate-limit.catalog.refill-window-seconds}") long refillWindowSeconds) {
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
    if (!enabled || !isCatalogRead(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String key = clientIpResolver.resolve(request) + ":catalog";
    RateLimitResult result = rateLimitService.consume("catalog", key, capacity, refillWindow);
    if (!result.allowed()) {
      responseWriter.write(request, response, result.retryAfterSeconds());
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean isCatalogRead(HttpServletRequest request) {
    return "GET".equals(request.getMethod()) && request.getRequestURI().startsWith("/api/v1/products");
  }

}
