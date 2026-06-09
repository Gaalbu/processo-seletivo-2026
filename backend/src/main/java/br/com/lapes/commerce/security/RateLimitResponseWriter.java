package br.com.lapes.commerce.security;

import br.com.lapes.commerce.common.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class RateLimitResponseWriter {

  private final ObjectMapper objectMapper;

  public RateLimitResponseWriter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public void write(HttpServletRequest request, HttpServletResponse response, long retryAfterSeconds)
      throws IOException {
    response.setStatus(429);
    response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(
        response.getWriter(),
        ApiError.of(429, "Too Many Requests", "Rate limit exceeded", request.getRequestURI()));
  }
}
