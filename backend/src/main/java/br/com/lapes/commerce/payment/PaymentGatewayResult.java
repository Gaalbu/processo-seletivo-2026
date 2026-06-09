package br.com.lapes.commerce.payment;

import br.com.lapes.commerce.domain.PaymentStatus;

public record PaymentGatewayResult(
    String providerPaymentId, PaymentStatus status, String checkoutUrl, String rawPayload) {}
