package br.com.lapes.commerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Enumerated(EnumType.STRING)
  @Column(name = "from_status", length = 20)
  private OrderStatus fromStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "to_status", nullable = false, length = 20)
  private OrderStatus toStatus;

  @Column(name = "changed_by")
  private String changedBy;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected OrderStatusHistory() {}

  public static OrderStatusHistory record(Order order, OrderStatus fromStatus, OrderStatus toStatus, String changedBy) {
    OrderStatusHistory history = new OrderStatusHistory();
    history.order = order;
    history.fromStatus = fromStatus;
    history.toStatus = toStatus;
    history.changedBy = changedBy;
    return history;
  }
}
