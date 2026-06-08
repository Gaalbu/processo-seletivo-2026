package br.com.lapes.commerce.product;

public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException() {
    super("Product not found");
  }
}
