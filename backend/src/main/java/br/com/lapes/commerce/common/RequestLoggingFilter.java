package br.com.lapes.commerce.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);

  private final ObjectMapper objectMapper;

  public RequestLoggingFilter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    long startedAt = System.nanoTime();

    try {
      filterChain.doFilter(request, response);
    } finally {
      long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
      Map<String, Object> log = new LinkedHashMap<>();
      log.put("timestamp", Instant.now().toString());
      log.put("method", request.getMethod());
      log.put("route", request.getRequestURI());
      log.put("statusCode", response.getStatus());
      log.put("durationMs", durationMs);
      try {
        LOGGER.info(objectMapper.writeValueAsString(log));
      } catch (Exception exception) {
        LOGGER.warn("Failed to serialize request log: {}", log, exception);
      }
    }
  }
}
