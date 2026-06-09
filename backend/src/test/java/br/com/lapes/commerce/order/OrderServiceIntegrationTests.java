package br.com.lapes.commerce.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.lapes.commerce.cart.InsufficientStockException;
import br.com.lapes.commerce.domain.OrderStatus;
import br.com.lapes.commerce.domain.PaymentStatus;
import br.com.lapes.commerce.repository.CartItemRepository;
import br.com.lapes.commerce.repository.CouponUsageRepository;
import br.com.lapes.commerce.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class OrderServiceIntegrationTests {

  private static final UUID USER_1 = UUID.fromString("00000000-0000-0000-0000-000000000101");
  private static final UUID USER_2 = UUID.fromString("00000000-0000-0000-0000-000000000102");
  private static final UUID CART_1 = UUID.fromString("10000000-0000-0000-0000-000000000101");
  private static final UUID CART_2 = UUID.fromString("10000000-0000-0000-0000-000000000102");
  private static final UUID PRODUCT_1 = UUID.fromString("20000000-0000-0000-0000-000000000101");
  private static final UUID PRODUCT_2 = UUID.fromString("20000000-0000-0000-0000-000000000102");
  private static final UUID COUPON_PERCENT = UUID.fromString("30000000-0000-0000-0000-000000000101");
  private static final UUID COUPON_MINIMUM = UUID.fromString("30000000-0000-0000-0000-000000000102");
  private static final UUID COUPON_EXPIRED = UUID.fromString("30000000-0000-0000-0000-000000000103");

  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private OrderService orderService;
  @Autowired private CartItemRepository cartItemRepository;
  @Autowired private CouponUsageRepository couponUsageRepository;
  @Autowired private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    cleanDatabase();
    seedUsersAndCarts();
  }

  @Test
  void checkoutApprovedReservesStockClearsCartAndSnapshotsPrice() {
    insertProduct(PRODUCT_1, "Teclado Test", "perifericos", "100.00", 5);
    insertCartItem(CART_1, PRODUCT_1, 2);

    OrderResponse response = orderService.checkout(USER_1, new CheckoutRequest(null, true));

    assertThat(response.status()).isEqualTo(OrderStatus.PAID);
    assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.APPROVED);
    assertThat(response.subtotalAmount()).isEqualByComparingTo("200.00");
    assertThat(response.discountAmount()).isEqualByComparingTo("0.00");
    assertThat(response.totalAmount()).isEqualByComparingTo("200.00");
    assertThat(response.items()).hasSize(1);
    assertThat(response.items().getFirst().unitPrice()).isEqualByComparingTo("100.00");
    assertThat(productRepository.findById(PRODUCT_1).orElseThrow().getStock()).isEqualTo(3);
    assertThat(cartItemRepository.findByCartIdOrderByCreatedAtAsc(CART_1)).isEmpty();
  }

  @Test
  void checkoutPaymentFailureDoesNotReserveStockOrClearCart() {
    insertProduct(PRODUCT_1, "Teclado Test", "perifericos", "100.00", 5);
    insertCartItem(CART_1, PRODUCT_1, 2);

    OrderResponse response = orderService.checkout(USER_1, new CheckoutRequest(null, false));

    assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
    assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.FAILED);
    assertThat(productRepository.findById(PRODUCT_1).orElseThrow().getStock()).isEqualTo(5);
    assertThat(cartItemRepository.findByCartIdOrderByCreatedAtAsc(CART_1)).hasSize(1);
  }

  @Test
  void checkoutAppliesPercentageCouponAndMarksItAsUsed() {
    insertProduct(PRODUCT_1, "Teclado Test", "perifericos", "100.00", 5);
    insertCartItem(CART_1, PRODUCT_1, 2);
    insertCoupon(COUPON_PERCENT, "SAVE10", "PERCENTAGE", "10.00", "100.00", "2030-01-01T00:00:00Z", true);

    OrderResponse response = orderService.checkout(USER_1, new CheckoutRequest("SAVE10", true));

    assertThat(response.discountAmount()).isEqualByComparingTo("20.00");
    assertThat(response.totalAmount()).isEqualByComparingTo("180.00");
    assertThat(couponUsageRepository.existsByCouponIdAndUserId(COUPON_PERCENT, USER_1)).isTrue();
  }

  @Test
  void checkoutRejectsExpiredCoupon() {
    insertProduct(PRODUCT_1, "Teclado Test", "perifericos", "100.00", 5);
    insertCartItem(CART_1, PRODUCT_1, 2);
    insertCoupon(COUPON_EXPIRED, "OLD10", "PERCENTAGE", "10.00", "100.00", "2020-01-01T00:00:00Z", true);

    assertThatThrownBy(() -> orderService.checkout(USER_1, new CheckoutRequest("OLD10", true)))
        .isInstanceOf(InvalidCouponException.class)
        .hasMessage("Coupon is expired or inactive");
  }

  @Test
  void checkoutRejectsCouponWhenMinimumOrderAmountWasNotReached() {
    insertProduct(PRODUCT_1, "Teclado Test", "perifericos", "100.00", 5);
    insertCartItem(CART_1, PRODUCT_1, 1);
    insertCoupon(COUPON_MINIMUM, "MIN200", "FIXED_AMOUNT", "20.00", "200.00", "2030-01-01T00:00:00Z", true);

    assertThatThrownBy(() -> orderService.checkout(USER_1, new CheckoutRequest("MIN200", true)))
        .isInstanceOf(InvalidCouponException.class)
        .hasMessage("Order minimum amount was not reached for this coupon");
  }

  @Test
  void checkoutRejectsCouponAlreadyUsedByUser() {
    insertProduct(PRODUCT_1, "Teclado Test", "perifericos", "100.00", 5);
    insertProduct(PRODUCT_2, "Mouse Test", "perifericos", "100.00", 5);
    insertCoupon(COUPON_PERCENT, "SAVE10", "PERCENTAGE", "10.00", "100.00", "2030-01-01T00:00:00Z", true);
    insertCartItem(CART_1, PRODUCT_1, 1);
    orderService.checkout(USER_1, new CheckoutRequest("SAVE10", true));
    insertCartItem(CART_1, PRODUCT_2, 1);

    assertThatThrownBy(() -> orderService.checkout(USER_1, new CheckoutRequest("SAVE10", true)))
        .isInstanceOf(InvalidCouponException.class)
        .hasMessage("Coupon was already used by this user");
  }

  @Test
  void concurrentCheckoutAllowsOnlyOneUserToBuyLastItem() throws Exception {
    insertProduct(PRODUCT_1, "Último Teclado", "perifericos", "100.00", 1);
    insertCartItem(CART_1, PRODUCT_1, 1);
    insertCartItem(CART_2, PRODUCT_1, 1);

    CountDownLatch start = new CountDownLatch(1);
    var executor = Executors.newFixedThreadPool(2);
    Callable<Boolean> userOneCheckout = () -> checkoutWhenReleased(start, USER_1);
    Callable<Boolean> userTwoCheckout = () -> checkoutWhenReleased(start, USER_2);

    var futures = List.of(executor.submit(userOneCheckout), executor.submit(userTwoCheckout));
    start.countDown();

    int successes = 0;
    int failures = 0;
    for (var future : futures) {
      if (future.get(10, TimeUnit.SECONDS)) {
        successes++;
      } else {
        failures++;
      }
    }
    executor.shutdownNow();

    assertThat(successes).isEqualTo(1);
    assertThat(failures).isEqualTo(1);
    assertThat(productRepository.findById(PRODUCT_1).orElseThrow().getStock()).isZero();
  }

  private boolean checkoutWhenReleased(CountDownLatch start, UUID userId) throws InterruptedException {
    start.await();
    try {
      orderService.checkout(userId, new CheckoutRequest(null, true));
      return true;
    } catch (InsufficientStockException exception) {
      return false;
    }
  }

  private void cleanDatabase() {
    jdbcTemplate.update("delete from coupon_usages");
    jdbcTemplate.update("delete from order_items");
    jdbcTemplate.update("delete from orders");
    jdbcTemplate.update("delete from cart_items");
    jdbcTemplate.update("delete from carts");
    jdbcTemplate.update("delete from coupons");
    jdbcTemplate.update("delete from products");
    jdbcTemplate.update("delete from users");
  }

  private void seedUsersAndCarts() {
    insertUser(USER_1, "Cliente 1", "cliente1@test.local");
    insertUser(USER_2, "Cliente 2", "cliente2@test.local");
    insertCart(CART_1, USER_1);
    insertCart(CART_2, USER_2);
  }

  private void insertUser(UUID id, String name, String email) {
    jdbcTemplate.update(
        "insert into users (id, name, email, password_hash, role, created_at, updated_at) values (?, ?, ?, ?, 'CUSTOMER', current_timestamp, current_timestamp)",
        id,
        name,
        email,
        "password-hash");
  }

  private void insertCart(UUID id, UUID userId) {
    jdbcTemplate.update(
        "insert into carts (id, user_id, created_at, updated_at) values (?, ?, current_timestamp, current_timestamp)",
        id,
        userId);
  }

  private void insertProduct(UUID id, String name, String category, String price, int stock) {
    jdbcTemplate.update(
        "insert into products (id, name, description, price, stock, category, image_url, version, created_at, updated_at) values (?, ?, 'Produto de teste', ?, ?, ?, 'https://example.com/image.png', 0, current_timestamp, current_timestamp)",
        id,
        name,
        new BigDecimal(price),
        stock,
        category);
  }

  private void insertCartItem(UUID cartId, UUID productId, int quantity) {
    jdbcTemplate.update(
        "insert into cart_items (id, cart_id, product_id, quantity, created_at, updated_at) values (?, ?, ?, ?, current_timestamp, current_timestamp)",
        UUID.randomUUID(),
        cartId,
        productId,
        quantity);
  }

  private void insertCoupon(
      UUID id,
      String code,
      String type,
      String value,
      String minimumOrderAmount,
      String expiresAt,
      boolean active) {
    jdbcTemplate.update(
        "insert into coupons (id, code, type, discount_value, minimum_order_amount, expires_at, active, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?, current_timestamp, current_timestamp)",
        id,
        code,
        type,
        new BigDecimal(value),
        new BigDecimal(minimumOrderAmount),
        OffsetDateTime.parse(expiresAt),
        active);
  }
}
