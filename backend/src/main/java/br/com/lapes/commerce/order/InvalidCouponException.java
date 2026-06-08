package br.com.lapes.commerce.order;

public class InvalidCouponException extends RuntimeException {

  public InvalidCouponException(String message) {
    super(message);
  }
}
