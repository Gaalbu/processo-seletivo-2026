package br.com.lapes.commerce.order;

import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record CheckoutRequest(
    @Schema(example = "TERMINAL10") @Size(max = 40) String couponCode,
    @Schema(example = "true", description = "false simula falha de pagamento") Boolean paymentApproved,
    @Schema(description = "Chave de idempotencia fornecida pelo cliente") @Size(max = 120) String idempotencyKey) {}
