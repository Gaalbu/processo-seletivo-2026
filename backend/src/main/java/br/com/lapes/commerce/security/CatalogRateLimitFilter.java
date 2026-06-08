package br.com.lapes.commerce.security;

import br.com.lapes.commerce.common.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(2)
public class CatalogRateLimitFilter extends OncePerRequestFilter {

  private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper;
  private final int capacity;
  private final Duration refillWindow;

  public CatalogRateLimitFilter(
      ObjectMapper objectMapper,
      @Value("${app.rate-limit.catalog.capacity}") int capacity,
      @Value("${app.rate-limit.catalog.refill-window-seconds}") long refillWindowSeconds) {
    this.objectMapper = objectMapper;
    this.capacity = capacity;
    this.refillWindow = Duration.ofSeconds(refillWindowSeconds);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (!isCatalogRead(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String key = request.getRemoteAddr() + ":catalog";
    Window window = windows.compute(key, (ignored, current) -> nextWindow(current));
    if (window.requests() > capacity) {
      response.setStatus(429);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      objectMapper.writeValue(
          response.getWriter(),
          ApiError.of(429, "Too Many Requests", "Rate limit exceeded", request.getRequestURI()));
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean isCatalogRead(HttpServletRequest request) {
    return "GET".equals(request.getMethod()) && request.getRequestURI().startsWith("/api/products");
  }

  private Window nextWindow(Window current) {
    Instant now = Instant.now();
    if (current == null || now.isAfter(current.startedAt().plus(refillWindow))) {
      return new Window(now, 1);
    }
    return new Window(current.startedAt(), current.requests() + 1);
  }

  private record Window(Instant startedAt, int requests) {}
}
