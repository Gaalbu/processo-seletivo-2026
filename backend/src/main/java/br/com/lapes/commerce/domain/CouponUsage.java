package br.com.lapes.commerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "coupon_usages")
public class CouponUsage {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "coupon_id", nullable = false)
  private Coupon coupon;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @CreationTimestamp
  @Column(name = "used_at", nullable = false, updatable = false)
  private Instant usedAt;

  protected CouponUsage() {}

  public static CouponUsage create(Coupon coupon, User user, Order order) {
    CouponUsage usage = new CouponUsage();
    usage.coupon = coupon;
    usage.user = user;
    usage.order = order;
    return usage;
  }

  public UUID getId() {
    return id;
  }
}
