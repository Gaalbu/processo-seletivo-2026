package br.com.lapes.commerce.payment;

public class InvalidPaymentWebhookException extends RuntimeException {

  public InvalidPaymentWebhookException() {
    super("Invalid payment webhook signature");
  }
}
