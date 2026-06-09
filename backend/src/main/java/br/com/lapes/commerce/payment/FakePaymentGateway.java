package br.com.lapes.commerce.payment;

import br.com.lapes.commerce.domain.PaymentStatus;
import java.util.UUID;

public class FakePaymentGateway implements PaymentGateway {

  @Override
  public String provider() {
    return "fake";
  }

  @Override
  public PaymentGatewayResult createPayment(PaymentGatewayRequest request) {
    PaymentStatus status = Boolean.FALSE.equals(request.simulatedApproved()) ? PaymentStatus.FAILED : PaymentStatus.APPROVED;
    String providerPaymentId = "fake_" + UUID.randomUUID();
    String rawPayload = "{\"provider\":\"fake\",\"status\":\"" + status + "\"}";
    return new PaymentGatewayResult(providerPaymentId, status, null, rawPayload);
  }

  @Override
  public PaymentGatewayStatus fetchPaymentStatus(String providerPaymentId) {
    return new PaymentGatewayStatus(PaymentStatus.APPROVED, "{\"provider\":\"fake\"}", null);
  }
}
