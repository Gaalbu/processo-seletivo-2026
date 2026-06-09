package br.com.lapes.commerce.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RateLimitResponseWriterTests {

  @Test
  void writesTooManyRequestsWithRetryAfter() throws Exception {
    RateLimitResponseWriter writer = new RateLimitResponseWriter(new ObjectMapper().findAndRegisterModules());
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
    MockHttpServletResponse response = new MockHttpServletResponse();

    writer.write(request, response, 42);

    assertThat(response.getStatus()).isEqualTo(429);
    assertThat(response.getHeader(HttpHeaders.RETRY_AFTER)).isEqualTo("42");
    assertThat(response.getContentAsString()).contains("Rate limit exceeded");
  }
}
