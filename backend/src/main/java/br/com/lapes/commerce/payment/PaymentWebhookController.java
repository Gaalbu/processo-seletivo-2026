package br.com.lapes.commerce.payment;

import br.com.lapes.commerce.order.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentWebhookController {

  private final OrderService orderService;
  private final PaymentWebhookVerifier webhookVerifier;

  public PaymentWebhookController(OrderService orderService, PaymentWebhookVerifier webhookVerifier) {
    this.orderService = orderService;
    this.webhookVerifier = webhookVerifier;
  }

  @PostMapping("/mercado-pago/webhook")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void mercadoPagoWebhook(
      @RequestHeader(name = "x-request-id", required = false) String requestId,
      @RequestHeader(name = "x-signature", required = false) String signature,
      @RequestBody JsonNode payload) {
    if (!webhookVerifier.isValid(requestId, signature, payload)) {
      throw new InvalidPaymentWebhookException();
    }

    String providerPaymentId = payload.path("data").path("id").asText(null);
    if (providerPaymentId == null || providerPaymentId.isBlank()) {
      return;
    }
    orderService.syncPayment(providerPaymentId);
  }
}
