package br.com.lapes.commerce.order;

public class OrderNotFoundException extends RuntimeException {

  public OrderNotFoundException() {
    super("Order not found");
  }
}
