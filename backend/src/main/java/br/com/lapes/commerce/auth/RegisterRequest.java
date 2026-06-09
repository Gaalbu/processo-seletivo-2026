package br.com.lapes.commerce.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterRequest(
    @Schema(example = "Cliente LAPES") @NotBlank @Size(max = 120) String name,
    @Schema(example = "cliente@lapes.test") @NotBlank @Email @Size(max = 160) String email,
    @Schema(example = "password123") @NotBlank @Size(min = 8, max = 72) String password) {}
