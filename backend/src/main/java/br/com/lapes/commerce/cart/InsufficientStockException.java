package br.com.lapes.commerce.cart;

public class InsufficientStockException extends RuntimeException {

  public InsufficientStockException() {
    super("Insufficient stock for requested quantity");
  }
}
