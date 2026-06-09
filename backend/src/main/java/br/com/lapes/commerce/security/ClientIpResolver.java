package br.com.lapes.commerce.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIpResolver {

  public String resolve(HttpServletRequest request) {
    String forwardedFor = firstHeaderValue(request.getHeader("X-Forwarded-For"));
    if (forwardedFor != null && !forwardedFor.isBlank()) {
      return forwardedFor;
    }

    String realIp = request.getHeader("X-Real-IP");
    if (realIp != null && !realIp.isBlank()) {
      return realIp.trim();
    }

    return request.getRemoteAddr();
  }

  private String firstHeaderValue(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.split(",", 2)[0].trim();
  }
}
