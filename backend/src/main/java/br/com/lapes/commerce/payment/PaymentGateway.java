package br.com.lapes.commerce.payment;

public interface PaymentGateway {

  String provider();

  PaymentGatewayResult createPayment(PaymentGatewayRequest request);

  PaymentGatewayStatus fetchPaymentStatus(String providerPaymentId);
}
