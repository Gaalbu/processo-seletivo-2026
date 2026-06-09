package br.com.lapes.commerce.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class ClientIpResolverTests {

  private final ClientIpResolver resolver = new ClientIpResolver();

  @Test
  void resolvesFirstForwardedIp() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-Forwarded-For", "203.0.113.10, 10.0.0.1");

    assertThat(resolver.resolve(request)).isEqualTo("203.0.113.10");
  }

  @Test
  void fallsBackToRemoteAddress() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRemoteAddr("127.0.0.1");

    assertThat(resolver.resolve(request)).isEqualTo("127.0.0.1");
  }
}
