package br.com.lapes.commerce.order;

import br.com.lapes.commerce.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
