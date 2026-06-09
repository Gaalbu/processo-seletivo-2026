package br.com.lapes.commerce.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateCartItemRequest(@Schema(example = "2") @NotNull @Min(1) Integer quantity) {}
