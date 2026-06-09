package br.com.lapes.commerce.order;

import br.com.lapes.commerce.domain.Order;
import br.com.lapes.commerce.domain.OrderStatus;
import br.com.lapes.commerce.domain.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    OrderStatus status,
    PaymentStatus paymentStatus,
    String couponCode,
    BigDecimal subtotalAmount,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    String paymentUrl,
    Instant createdAt,
    List<OrderItemResponse> items) {

  public static OrderResponse from(Order order, List<OrderItemResponse> items) {
    return from(order, items, null);
  }

  public static OrderResponse from(Order order, List<OrderItemResponse> items, String paymentUrl) {
    return new OrderResponse(
        order.getId(),
        order.getStatus(),
        order.getPaymentStatus(),
        order.getCoupon() == null ? null : order.getCoupon().getCode(),
        order.getSubtotalAmount(),
        order.getDiscountAmount(),
        order.getTotalAmount(),
        paymentUrl,
        order.getCreatedAt(),
        items);
  }
}
