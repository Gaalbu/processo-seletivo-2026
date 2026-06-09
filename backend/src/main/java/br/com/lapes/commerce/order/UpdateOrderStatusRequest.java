package br.com.lapes.commerce.order;

import br.com.lapes.commerce.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateOrderStatusRequest(@Schema(example = "SHIPPED") @NotNull OrderStatus status) {}
