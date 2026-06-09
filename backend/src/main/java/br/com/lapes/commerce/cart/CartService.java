package br.com.lapes.commerce.cart;

import br.com.lapes.commerce.domain.Cart;
import br.com.lapes.commerce.domain.CartItem;
import br.com.lapes.commerce.domain.Product;
import br.com.lapes.commerce.product.ProductNotFoundException;
import br.com.lapes.commerce.repository.CartItemRepository;
import br.com.lapes.commerce.repository.CartRepository;
import br.com.lapes.commerce.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final ProductRepository productRepository;

  public CartService(
      CartRepository cartRepository,
      CartItemRepository cartItemRepository,
      ProductRepository productRepository) {
    this.cartRepository = cartRepository;
    this.cartItemRepository = cartItemRepository;
    this.productRepository = productRepository;
  }

  @Transactional(readOnly = true)
  public CartResponse getCart(UUID userId) {
    Cart cart = cart(userId);
    List<CartItem> items = cartItemRepository.findByCartIdOrderByCreatedAtAsc(cart.getId());
    List<CartItemResponse> activeItems = new ArrayList<>(items.size());

    for (CartItem item : items) {
      if (productRepository.findDeletedAtById(item.getProduct().getId()) != null) {
        cartItemRepository.delete(item);
        continue;
      }
      Product product = item.getProduct();
      activeItems.add(toResponse(item, product));
    }

    return toResponse(cart, activeItems);
  }

  @Transactional
  public CartResponse addItem(UUID userId, AddCartItemRequest request) {
    Cart cart = cart(userId);
    Product product = activeProduct(request.productId());

    CartItem item =
        cartItemRepository
            .findByCartIdAndProductId(cart.getId(), product.getId())
            .orElseGet(() -> CartItem.create(cart, product, 0));

    int newQuantity = item.getQuantity() + request.quantity();
    ensureStock(product, newQuantity);
    item.updateQuantity(newQuantity);
    cartItemRepository.save(item);

    return getCart(userId);
  }

  @Transactional
  public CartResponse updateItem(UUID userId, UUID itemId, UpdateCartItemRequest request) {
    Cart cart = cart(userId);
    CartItem item =
        cartItemRepository
            .findById(itemId)
            .filter(cartItem -> cartItem.getCart().getId().equals(cart.getId()))
            .orElseThrow(CartItemNotFoundException::new);

    Product product = item.getProduct();
    ensureActive(product);
    ensureStock(product, request.quantity());
    item.updateQuantity(request.quantity());

    return getCart(userId);
  }

  @Transactional
  public void removeItem(UUID userId, UUID itemId) {
    Cart cart = cart(userId);
    CartItem item =
        cartItemRepository
            .findById(itemId)
            .filter(cartItem -> cartItem.getCart().getId().equals(cart.getId()))
            .orElseThrow(CartItemNotFoundException::new);
    cartItemRepository.delete(item);
  }

  @Transactional
  public void clear(UUID userId) {
    Cart cart = cart(userId);
    cartItemRepository.deleteByCartId(cart.getId());
  }

  private Cart cart(UUID userId) {
    return cartRepository.findByUserId(userId).orElseThrow(CartNotFoundException::new);
  }

  private Product activeProduct(UUID productId) {
    Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
    ensureActive(product);
    return product;
  }

  private void ensureActive(Product product) {
    if (product.getDeletedAt() != null) {
      throw new ProductNotFoundException();
    }
  }

  private void ensureStock(Product product, int quantity) {
    if (quantity > product.getStock()) {
      throw new InsufficientStockException();
    }
  }

  private CartResponse toResponse(Cart cart, List<CartItemResponse> itemResponses) {
    BigDecimal subtotal =
        itemResponses.stream()
            .map(CartItemResponse::lineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new CartResponse(cart.getId(), itemResponses, subtotal);
  }

  private CartItemResponse toResponse(CartItem item, Product product) {
    BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    return new CartItemResponse(
        item.getId(),
        product.getId(),
        product.getName(),
        product.getPrice(),
        item.getQuantity(),
        product.getStock(),
        product.getImageUrl(),
        lineTotal);
  }
}
