package br.com.lapes.commerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "coupons")
public class Coupon {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false, unique = true, length = 40)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private CouponType type;

  @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
  private BigDecimal value;

  @Column(name = "minimum_order_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal minimumOrderAmount;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(nullable = false)
  private Boolean active;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected Coupon() {}

  public static Coupon create(String code, CouponType type, BigDecimal value, BigDecimal minimumOrderAmount, Instant expiresAt, boolean active) {
    Coupon coupon = new Coupon();
    coupon.code = code;
    coupon.type = type;
    coupon.value = value;
    coupon.minimumOrderAmount = minimumOrderAmount;
    coupon.expiresAt = expiresAt;
    coupon.active = active;
    return coupon;
  }

  public UUID getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public CouponType getType() {
    return type;
  }

  public BigDecimal getValue() {
    return value;
  }

  public BigDecimal getMinimumOrderAmount() {
    return minimumOrderAmount;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public Boolean getActive() {
    return active;
  }
}
