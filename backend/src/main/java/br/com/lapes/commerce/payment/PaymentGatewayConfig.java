package br.com.lapes.commerce.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentGatewayConfig {

  @Bean
  public PaymentGateway paymentGateway(
      RestClient.Builder restClientBuilder,
      ObjectMapper objectMapper,
      @Value("${app.payment.mercado-pago.access-token:}") String accessToken,
      @Value("${app.payment.notification-url:http://localhost:8080/api/payments/mercado-pago/webhook}") String notificationUrl,
      @Value("${app.payment.success-url:http://localhost:3000/orders}") String successUrl,
      @Value("${app.payment.failure-url:http://localhost:3000/orders}") String failureUrl,
      @Value("${app.payment.pending-url:http://localhost:3000/orders}") String pendingUrl) {
    if (accessToken == null || accessToken.isBlank()) {
      return new FakePaymentGateway();
    }
    return new MercadoPagoPaymentGateway(
        restClientBuilder,
        objectMapper,
        accessToken,
        notificationUrl,
        successUrl,
        failureUrl,
        pendingUrl);
  }
}
