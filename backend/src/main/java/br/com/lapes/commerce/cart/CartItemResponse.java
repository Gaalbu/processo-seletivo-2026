package br.com.lapes.commerce.cart;

import br.com.lapes.commerce.domain.CartItem;
import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
    UUID id,
    UUID productId,
    String productName,
    BigDecimal unitPrice,
    Integer quantity,
    Integer availableStock,
    String imageUrl,
    BigDecimal lineTotal) {

  public static CartItemResponse from(CartItem item) {
    BigDecimal lineTotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    return new CartItemResponse(
        item.getId(),
        item.getProduct().getId(),
        item.getProduct().getName(),
        item.getProduct().getPrice(),
        item.getQuantity(),
        item.getProduct().getStock(),
        item.getProduct().getImageUrl(),
        lineTotal);
  }
}
