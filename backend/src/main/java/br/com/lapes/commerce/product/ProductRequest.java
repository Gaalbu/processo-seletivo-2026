package br.com.lapes.commerce.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record ProductRequest(
    @Schema(example = "Teclado Mecânico Terminal Pro") @NotBlank @Size(max = 160) String name,
    @Schema(example = "Teclado mecânico compacto com switches táteis.") @NotBlank String description,
    @Schema(example = "429.90") @NotNull @DecimalMin("0.00") BigDecimal price,
    @Schema(example = "12") @NotNull @Min(0) Integer stock,
    @Schema(example = "perifericos") @NotBlank @Size(max = 80) String category,
    @Schema(example = "https://example.com/keyboard.png") @NotBlank @Size(max = 500) String imageUrl) {}
