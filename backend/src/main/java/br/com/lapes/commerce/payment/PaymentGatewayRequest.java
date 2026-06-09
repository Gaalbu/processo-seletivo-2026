package br.com.lapes.commerce.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentGatewayRequest(
    UUID orderId,
    BigDecimal amount,
    String description,
    String customerEmail,
    String idempotencyKey,
    Boolean simulatedApproved) {}
