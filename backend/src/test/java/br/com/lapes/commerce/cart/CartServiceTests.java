package br.com.lapes.commerce.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.lapes.commerce.domain.Cart;
import br.com.lapes.commerce.domain.CartItem;
import br.com.lapes.commerce.domain.Product;
import br.com.lapes.commerce.domain.User;
import br.com.lapes.commerce.repository.CartItemRepository;
import br.com.lapes.commerce.repository.CartRepository;
import br.com.lapes.commerce.repository.ProductRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTests {

  @Mock private CartRepository cartRepository;
  @Mock private CartItemRepository cartItemRepository;
  @Mock private ProductRepository productRepository;
  @InjectMocks private CartService cartService;

  @Test
  void getCartRemovesItemsWhoseProductWasDeleted() {
    UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000201");
    UUID cartId = UUID.fromString("10000000-0000-0000-0000-000000000201");
    UUID productId = UUID.fromString("20000000-0000-0000-0000-000000000201");

    User user = User.customer("Cliente Teste", "cliente@test.local", "password-hash");
    setField(user, "id", userId);

    Cart cart = Cart.forUser(user);
    setField(cart, "id", cartId);

    Product product = Product.create("Produto Removido", "Descrição", new java.math.BigDecimal("99.90"), 5, "perifericos", "https://example.com/image.png");
    setField(product, "id", productId);
    setField(product, "deletedAt", Instant.now());

    CartItem item = CartItem.create(cart, product, 1);
    setField(item, "id", UUID.fromString("30000000-0000-0000-0000-000000000201"));

    when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
    when(cartItemRepository.findByCartIdOrderByCreatedAtAsc(cartId)).thenReturn(List.of(item));
    when(productRepository.findDeletedAtById(productId)).thenReturn(Instant.now());

    CartResponse response = cartService.getCart(userId);

    assertThat(response.items()).isEmpty();
    assertThat(response.subtotal()).isEqualByComparingTo("0.00");
    verify(cartItemRepository).delete(item);
  }

  private static void setField(Object target, String fieldName, Object value) {
    try {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (ReflectiveOperationException exception) {
      throw new AssertionError(exception);
    }
  }
}
