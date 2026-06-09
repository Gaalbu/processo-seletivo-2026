package br.com.lapes.commerce.payment;

import br.com.lapes.commerce.domain.PaymentStatus;

public record PaymentGatewayStatus(PaymentStatus status, String rawPayload, String externalReference) {}
