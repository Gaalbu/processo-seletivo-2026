package br.com.lapes.commerce.order;

public class InvalidOrderTransitionException extends RuntimeException {

  public InvalidOrderTransitionException() {
    super("Invalid order status transition");
  }
}
