package br.com.lapes.commerce.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
    @Schema(example = "cliente@lapes.test") @NotBlank @Email String email,
    @Schema(example = "password123") @NotBlank String password) {}
