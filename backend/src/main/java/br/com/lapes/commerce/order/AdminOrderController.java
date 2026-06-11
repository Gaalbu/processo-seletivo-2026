package br.com.lapes.commerce.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Orders", description = "Admin order management")
public class AdminOrderController {

  private final OrderService orderService;

  public AdminOrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping
  @Operation(summary = "List all orders")
  public List<OrderResponse> listAll() {
    return orderService.listAll();
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "Update order status")
  public OrderResponse updateStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateOrderStatusRequest request) {
    return orderService.updateStatus(id, request.status());
  }

  @PostMapping("/{id}/cancel")
  @Operation(summary = "Cancel order before shipping")
  public OrderResponse cancel(@PathVariable UUID id) {
    return orderService.cancelAdmin(id);
  }
}
