package br.com.lapes.commerce.payment;

public class PaymentGatewayException extends RuntimeException {

  public PaymentGatewayException(String message) {
    super(message);
  }

  public PaymentGatewayException(String message, Throwable cause) {
    super(message, cause);
  }
}
