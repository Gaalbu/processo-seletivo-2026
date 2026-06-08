package br.com.lapes.commerce.product;

import br.com.lapes.commerce.domain.Product;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    String category,
    String imageUrl)
    implements Serializable {

  public static ProductResponse from(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.getStock(),
        product.getCategory(),
        product.getImageUrl());
  }
}
