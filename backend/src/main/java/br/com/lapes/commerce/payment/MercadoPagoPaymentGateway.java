package br.com.lapes.commerce.payment;

import br.com.lapes.commerce.domain.PaymentStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;

public class MercadoPagoPaymentGateway implements PaymentGateway {

  private static final String API_URL = "https://api.mercadopago.com";

  private final RestClient restClient;
  private final ObjectMapper objectMapper;
  private final String notificationUrl;
  private final String successUrl;
  private final String failureUrl;
  private final String pendingUrl;

  public MercadoPagoPaymentGateway(
      RestClient.Builder restClientBuilder,
      ObjectMapper objectMapper,
      String accessToken,
      String notificationUrl,
      String successUrl,
      String failureUrl,
      String pendingUrl) {
    this.restClient =
        restClientBuilder
            .baseUrl(API_URL)
            .defaultHeader("Authorization", "Bearer " + accessToken)
            .build();
    this.objectMapper = objectMapper;
    this.notificationUrl = notificationUrl;
    this.successUrl = successUrl;
    this.failureUrl = failureUrl;
    this.pendingUrl = pendingUrl;
  }

  @Override
  public String provider() {
    return "mercado_pago";
  }

  @Override
  public PaymentGatewayResult createPayment(PaymentGatewayRequest request) {
    Map<String, Object> payload = preferencePayload(request);
    JsonNode response;
    try {
      response =
          restClient
              .post()
              .uri("/checkout/preferences")
              .header("X-Idempotency-Key", request.idempotencyKey())
              .body(payload)
              .retrieve()
              .body(JsonNode.class);
    } catch (RestClientException exception) {
      throw new PaymentGatewayException("Could not create Mercado Pago checkout", exception);
    }

    if (response == null || response.path("id").asText().isBlank()) {
      throw new PaymentGatewayException("Mercado Pago did not return a preference id");
    }

    String checkoutUrl = response.path("init_point").asText(null);
    if (checkoutUrl == null || checkoutUrl.isBlank()) {
      checkoutUrl = response.path("sandbox_init_point").asText(null);
    }

    return new PaymentGatewayResult(
        response.path("id").asText(), PaymentStatus.PENDING, checkoutUrl, toJson(response));
  }

  @Override
  public PaymentGatewayStatus fetchPaymentStatus(String providerPaymentId) {
    JsonNode response;
    try {
      response =
          restClient.get().uri("/v1/payments/{id}", providerPaymentId).retrieve().body(JsonNode.class);
    } catch (RestClientException exception) {
      throw new PaymentGatewayException("Could not fetch Mercado Pago payment status", exception);
    }
    if (response == null) {
      throw new PaymentGatewayException("Mercado Pago did not return payment details");
    }
    return new PaymentGatewayStatus(
        mapStatus(response.path("status").asText()),
        toJson(response),
        response.path("external_reference").asText(null));
  }

  private Map<String, Object> preferencePayload(PaymentGatewayRequest request) {
    return Map.of(
        "items",
        List.of(
            Map.of(
                "title", request.description(),
                "quantity", 1,
                "currency_id", "BRL",
                "unit_price", request.amount())),
        "payer",
        Map.of("email", request.customerEmail()),
        "external_reference",
        request.orderId().toString(),
        "notification_url",
        notificationUrl,
        "back_urls",
        Map.of("success", successUrl, "failure", failureUrl, "pending", pendingUrl),
        "auto_return",
        "approved");
  }

  private PaymentStatus mapStatus(String status) {
    return switch (status) {
      case "approved" -> PaymentStatus.APPROVED;
      case "rejected", "cancelled", "refunded", "charged_back" -> PaymentStatus.FAILED;
      default -> PaymentStatus.PENDING;
    };
  }

  private String toJson(JsonNode node) {
    try {
      return objectMapper.writeValueAsString(node);
    } catch (JsonProcessingException exception) {
      return "{}";
    }
  }
}
