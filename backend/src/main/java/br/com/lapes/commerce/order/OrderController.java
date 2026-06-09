package br.com.lapes.commerce.order;

import br.com.lapes.commerce.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('CUSTOMER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orders", description = "Customer orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping
  @Operation(summary = "List my orders")
  public List<OrderResponse> listMine(@AuthenticationPrincipal AuthenticatedUser user) {
    return orderService.listMine(user.id());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get my order")
  public OrderResponse detailMine(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
    return orderService.detailMine(user.id(), id);
  }

  @PostMapping("/{id}/cancel")
  @Operation(summary = "Cancel my order before shipping")
  public OrderResponse cancelMine(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
    return orderService.cancelMine(user.id(), id);
  }
}
