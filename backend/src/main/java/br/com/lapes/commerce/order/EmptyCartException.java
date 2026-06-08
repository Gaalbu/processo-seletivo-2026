package br.com.lapes.commerce.order;

public class EmptyCartException extends RuntimeException {

  public EmptyCartException() {
    super("Cart is empty");
  }
}
