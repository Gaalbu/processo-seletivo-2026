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
@Order(1)
public class AuthRateLimitFilter extends OncePerRequestFilter {

  private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper;
  private final int capacity;
  private final Duration refillWindow;

  public AuthRateLimitFilter(
      ObjectMapper objectMapper,
      @Value("${app.rate-limit.auth.capacity}") int capacity,
      @Value("${app.rate-limit.auth.refill-window-seconds}") long refillWindowSeconds) {
    this.objectMapper = objectMapper;
    this.capacity = capacity;
    this.refillWindow = Duration.ofSeconds(refillWindowSeconds);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (!isAuthWrite(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String key = request.getRemoteAddr() + ":" + request.getRequestURI();
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

  private boolean isAuthWrite(HttpServletRequest request) {
    return "POST".equals(request.getMethod())
        && ("/api/auth/login".equals(request.getRequestURI())
            || "/api/auth/register".equals(request.getRequestURI()));
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
