package br.com.lapes.commerce.cart;

import br.com.lapes.commerce.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
@PreAuthorize("hasRole('CUSTOMER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cart", description = "Authenticated customer shopping cart")
public class CartController {

  private final CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping
  @Operation(summary = "Get current cart")
  public CartResponse getCart(@AuthenticationPrincipal AuthenticatedUser user) {
    return cartService.getCart(user.id());
  }

  @PostMapping("/items")
  @Operation(summary = "Add item to cart")
  public CartResponse addItem(
      @AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody AddCartItemRequest request) {
    return cartService.addItem(user.id(), request);
  }

  @PutMapping("/items/{itemId}")
  @Operation(summary = "Update cart item quantity")
  public CartResponse updateItem(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable UUID itemId,
      @Valid @RequestBody UpdateCartItemRequest request) {
    return cartService.updateItem(user.id(), itemId, request);
  }

  @DeleteMapping("/items/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Remove cart item")
  public void removeItem(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID itemId) {
    cartService.removeItem(user.id(), itemId);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Clear current cart")
  public void clear(@AuthenticationPrincipal AuthenticatedUser user) {
    cartService.clear(user.id());
  }
}
