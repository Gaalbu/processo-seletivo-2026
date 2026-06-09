package br.com.lapes.commerce.order;

import br.com.lapes.commerce.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
@PreAuthorize("hasRole('CUSTOMER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Checkout", description = "Checkout and payment simulation")
public class CheckoutController {

  private final OrderService orderService;

  public CheckoutController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  @Operation(summary = "Checkout current cart")
  public OrderResponse checkout(
      @AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody CheckoutRequest request) {
    return orderService.checkout(user.id(), request);
  }
}
