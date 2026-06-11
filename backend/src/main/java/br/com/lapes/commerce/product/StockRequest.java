package br.com.lapes.commerce.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockRequest(
    @NotNull @Min(0) Integer quantity) {
}
