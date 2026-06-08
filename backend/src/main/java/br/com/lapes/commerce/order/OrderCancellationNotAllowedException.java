package br.com.lapes.commerce.order;

public class OrderCancellationNotAllowedException extends RuntimeException {

  public OrderCancellationNotAllowedException() {
    super("Order cannot be cancelled after it is shipped");
  }
}
