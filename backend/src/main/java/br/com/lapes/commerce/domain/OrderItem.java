package br.com.lapes.commerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(name = "product_name", nullable = false, length = 160)
  private String productName;

  @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal unitPrice;

  @Column(nullable = false)
  private Integer quantity;

  @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
  private BigDecimal lineTotal;

  protected OrderItem() {}

  public static OrderItem create(Order order, Product product, Integer quantity) {
    OrderItem item = new OrderItem();
    item.order = order;
    item.product = product;
    item.productName = product.getName();
    item.unitPrice = product.getPrice();
    item.quantity = quantity;
    item.lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
    return item;
  }

  public UUID getId() {
    return id;
  }

  public Product getProduct() {
    return product;
  }

  public String getProductName() {
    return productName;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public BigDecimal getLineTotal() {
    return lineTotal;
  }
}
