package br.com.lapes.commerce.cart;

public class CartNotFoundException extends RuntimeException {

  public CartNotFoundException() {
    super("Cart not found");
  }
}
