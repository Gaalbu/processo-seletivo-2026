package br.com.lapes.commerce.order;

import jakarta.validation.constraints.Size;

public record CheckoutRequest(@Size(max = 40) String couponCode, Boolean paymentApproved) {}
