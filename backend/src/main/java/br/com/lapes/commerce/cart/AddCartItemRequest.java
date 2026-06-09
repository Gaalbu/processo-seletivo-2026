package br.com.lapes.commerce.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record AddCartItemRequest(
    @Schema(example = "20000000-0000-0000-0000-000000000001") @NotNull UUID productId,
    @Schema(example = "1") @NotNull @Min(1) Integer quantity) {}
