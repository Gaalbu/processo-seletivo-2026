package br.com.lapes.commerce.cart;

public class CartItemNotFoundException extends RuntimeException {

  public CartItemNotFoundException() {
    super("Cart item not found");
  }
}
