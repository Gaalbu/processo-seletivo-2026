package br.com.lapes.commerce.order;

import br.com.lapes.commerce.domain.OrderItem;
import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
    UUID id,
    UUID productId,
    String productName,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal lineTotal) {

  public static OrderItemResponse from(OrderItem item) {
    return new OrderItemResponse(
        item.getId(),
        item.getProduct().getId(),
        item.getProductName(),
        item.getUnitPrice(),
        item.getQuantity(),
        item.getLineTotal());
  }
}
