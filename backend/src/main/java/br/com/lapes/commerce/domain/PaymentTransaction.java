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
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Column(nullable = false, length = 40)
  private String provider;

  @Column(name = "provider_payment_id", nullable = false, length = 120)
  private String providerPaymentId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @Column(name = "checkout_url", length = 1000)
  private String checkoutUrl;

  @Column(name = "idempotency_key", nullable = false, unique = true, length = 120)
  private String idempotencyKey;

  @Column(name = "raw_payload", columnDefinition = "text")
  private String rawPayload;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected PaymentTransaction() {}

  public static PaymentTransaction create(
      Order order,
      String provider,
      String providerPaymentId,
      PaymentStatus status,
      BigDecimal amount,
      String checkoutUrl,
      String idempotencyKey,
      String rawPayload) {
    PaymentTransaction transaction = new PaymentTransaction();
    transaction.order = order;
    transaction.provider = provider;
    transaction.providerPaymentId = providerPaymentId;
    transaction.status = status;
    transaction.amount = amount;
    transaction.currency = "BRL";
    transaction.checkoutUrl = checkoutUrl;
    transaction.idempotencyKey = idempotencyKey;
    transaction.rawPayload = rawPayload;
    return transaction;
  }

  public void updateStatus(PaymentStatus status, String rawPayload) {
    this.status = status;
    this.rawPayload = rawPayload;
  }

  public void updateProviderPaymentId(String providerPaymentId) {
    this.providerPaymentId = providerPaymentId;
  }

  public UUID getId() {
    return id;
  }

  public Order getOrder() {
    return order;
  }

  public String getProvider() {
    return provider;
  }

  public String getProviderPaymentId() {
    return providerPaymentId;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getCheckoutUrl() {
    return checkoutUrl;
  }
}
