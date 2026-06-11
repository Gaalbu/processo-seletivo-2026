package br.com.lapes.commerce.order;

import br.com.lapes.commerce.cart.CartNotFoundException;
import br.com.lapes.commerce.cart.InsufficientStockException;
import br.com.lapes.commerce.domain.Cart;
import br.com.lapes.commerce.domain.CartItem;
import br.com.lapes.commerce.domain.Coupon;
import br.com.lapes.commerce.domain.CouponType;
import br.com.lapes.commerce.domain.CouponUsage;
import br.com.lapes.commerce.domain.Order;
import br.com.lapes.commerce.domain.OrderItem;
import br.com.lapes.commerce.domain.OrderStatus;
import br.com.lapes.commerce.domain.OrderStatusHistory;
import br.com.lapes.commerce.domain.PaymentStatus;
import br.com.lapes.commerce.domain.PaymentTransaction;
import br.com.lapes.commerce.domain.Product;
import br.com.lapes.commerce.domain.User;
import br.com.lapes.commerce.payment.PaymentGateway;
import br.com.lapes.commerce.payment.PaymentGatewayRequest;
import br.com.lapes.commerce.payment.PaymentGatewayResult;
import br.com.lapes.commerce.payment.PaymentGatewayStatus;
import br.com.lapes.commerce.product.ProductNotFoundException;
import br.com.lapes.commerce.repository.CartItemRepository;
import br.com.lapes.commerce.repository.CartRepository;
import br.com.lapes.commerce.repository.CouponRepository;
import br.com.lapes.commerce.repository.CouponUsageRepository;
import br.com.lapes.commerce.repository.OrderItemRepository;
import br.com.lapes.commerce.repository.OrderRepository;
import br.com.lapes.commerce.repository.OrderStatusHistoryRepository;
import br.com.lapes.commerce.repository.PaymentTransactionRepository;
import br.com.lapes.commerce.repository.ProductRepository;
import br.com.lapes.commerce.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final CouponRepository couponRepository;
  private final CouponUsageRepository couponUsageRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final OrderStatusHistoryRepository orderStatusHistoryRepository;
  private final PaymentGateway paymentGateway;

  public OrderService(
      CartRepository cartRepository,
      CartItemRepository cartItemRepository,
      CouponRepository couponRepository,
      CouponUsageRepository couponUsageRepository,
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository,
      PaymentTransactionRepository paymentTransactionRepository,
      ProductRepository productRepository,
      UserRepository userRepository,
      OrderStatusHistoryRepository orderStatusHistoryRepository,
      PaymentGateway paymentGateway) {
    this.cartRepository = cartRepository;
    this.cartItemRepository = cartItemRepository;
    this.couponRepository = couponRepository;
    this.couponUsageRepository = couponUsageRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.paymentTransactionRepository = paymentTransactionRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.orderStatusHistoryRepository = orderStatusHistoryRepository;
    this.paymentGateway = paymentGateway;
  }

  @Transactional
  @CacheEvict(cacheNames = {"products:list", "products:detail"}, allEntries = true)
  public OrderResponse checkout(UUID userId, CheckoutRequest request) {
    User user = userRepository.findById(userId).orElseThrow();
    Cart cart = cartRepository.findByUserId(userId).orElseThrow(CartNotFoundException::new);
    List<CartItem> cartItems = cartItemRepository.findByCartIdOrderByCreatedAtAsc(cart.getId());
    if (cartItems.isEmpty()) {
      throw new EmptyCartException();
    }

    List<Product> lockedProducts =
      cartItems.stream()
            .map(
                item ->
                    productRepository
                        .findActiveByIdForUpdate(item.getProduct().getId())
                        .orElseThrow(ProductNotFoundException::new))
            .toList();

    BigDecimal subtotal = BigDecimal.ZERO;
    for (int index = 0; index < cartItems.size(); index++) {
      CartItem item = cartItems.get(index);
      Product product = lockedProducts.get(index);
      if (item.getQuantity() > product.getStock()) {
        throw new InsufficientStockException();
      }
      subtotal = subtotal.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
    }

    Coupon coupon = resolveCoupon(request.couponCode(), userId, subtotal);
    BigDecimal discount = calculateDiscount(coupon, subtotal);
    BigDecimal total = subtotal.subtract(discount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    Order order =
        orderRepository.save(
            Order.create(
                user,
                coupon,
                subtotal.setScale(2, RoundingMode.HALF_UP),
                discount,
                total,
                PaymentStatus.PENDING));

    orderStatusHistoryRepository.save(
        OrderStatusHistory.record(order, null, order.getStatus(), user.getEmail()));

    List<OrderItem> orderItems = new ArrayList<>();
    for (int index = 0; index < cartItems.size(); index++) {
      orderItems.add(OrderItem.create(order, lockedProducts.get(index), cartItems.get(index).getQuantity()));
    }
    orderItemRepository.saveAll(orderItems);

    String idempotencyKey = "order-" + order.getId();
    PaymentGatewayResult payment =
        paymentGateway.createPayment(
            new PaymentGatewayRequest(
                order.getId(),
                total,
                "LAPES Commerce order " + order.getId(),
                user.getEmail(),
                idempotencyKey,
                request.paymentApproved()));
    paymentTransactionRepository.save(
        PaymentTransaction.create(
            order,
            paymentGateway.provider(),
            payment.providerPaymentId(),
            payment.status(),
            total,
            payment.checkoutUrl(),
            idempotencyKey,
            payment.rawPayload()));

    if (payment.status() == PaymentStatus.APPROVED) {
      markPendingOrderAsPaid(order);
      cartItemRepository.deleteByCartId(cart.getId());
    } else if (payment.status() == PaymentStatus.FAILED) {
      order.markPaymentFailed();
    }

    return toResponse(order, payment.checkoutUrl());
  }

  @Transactional
  @CacheEvict(cacheNames = {"products:list", "products:detail"}, allEntries = true)
  public void syncPayment(String providerPaymentId) {
    PaymentGatewayStatus status = paymentGateway.fetchPaymentStatus(providerPaymentId);
    Optional<PaymentTransaction> transactionByProviderId =
        paymentTransactionRepository.findByProviderAndProviderPaymentId(paymentGateway.provider(), providerPaymentId);
    PaymentTransaction transaction =
        transactionByProviderId.orElseGet(() -> findTransactionByExternalReference(status.externalReference()));

    if (!transaction.getProviderPaymentId().equals(providerPaymentId)) {
      transaction.updateProviderPaymentId(providerPaymentId);
    }
    if (transaction.getStatus() == status.status()) {
      return;
    }

    Order order = transaction.getOrder();
    if (status.status() == PaymentStatus.APPROVED) {
      if (order.getStatus() != OrderStatus.PAID) {
        markPendingOrderAsPaid(order);
        cartRepository.findByUserId(order.getUser().getId()).ifPresent(cart -> cartItemRepository.deleteByCartId(cart.getId()));
      }
    } else if (status.status() == PaymentStatus.FAILED) {
      order.markPaymentFailed();
    }
    transaction.updateStatus(status.status(), status.rawPayload());
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> listMine(UUID userId) {
    return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public OrderResponse detailMine(UUID userId, UUID orderId) {
    return toResponse(orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(OrderNotFoundException::new));
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> listAll() {
    return orderRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional
  @CacheEvict(cacheNames = {"products:list", "products:detail"}, allEntries = true)
  public OrderResponse cancelMine(UUID userId, UUID orderId) {
    Order order = orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(OrderNotFoundException::new);
    return cancel(order);
  }

  @Transactional
  @CacheEvict(cacheNames = {"products:list", "products:detail"}, allEntries = true)
  public OrderResponse cancelAdmin(UUID orderId) {
    return cancel(orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new));
  }

  @Transactional
  @CacheEvict(cacheNames = {"products:list", "products:detail"}, allEntries = true)
  public OrderResponse updateStatus(UUID orderId, OrderStatus nextStatus) {
    Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
    OrderStatus previous = order.getStatus();
    if (!isValidTransition(previous, nextStatus)) {
      throw new InvalidOrderTransitionException();
    }
    if (previous == OrderStatus.PENDING && nextStatus == OrderStatus.PAID) {
      markPendingOrderAsPaid(order);
    } else {
      order.updateStatus(nextStatus);
      orderStatusHistoryRepository.save(
          OrderStatusHistory.record(order, previous, nextStatus, "SYSTEM"));
    }
    return toResponse(order);
  }

  private void markPendingOrderAsPaid(Order order) {
    if (order.getStatus() == OrderStatus.PAID) {
      return;
    }
    for (OrderItem item : orderItemRepository.findByOrderId(order.getId())) {
      Product product =
          productRepository
              .findActiveByIdForUpdate(item.getProduct().getId())
              .orElseThrow(ProductNotFoundException::new);
      if (item.getQuantity() > product.getStock()) {
        throw new InsufficientStockException();
      }
      product.reserveStock(item.getQuantity());
    }
    if (order.getCoupon() != null
        && couponUsageRepository.existsByCouponIdAndUserId(
            order.getCoupon().getId(), order.getUser().getId())) {
      throw new InvalidCouponException("Coupon was already used by this user");
    }
    OrderStatus previous = order.getStatus();
    order.markPaid();
    orderStatusHistoryRepository.save(
        OrderStatusHistory.record(order, previous, OrderStatus.PAID, order.getUser().getEmail()));
    if (order.getCoupon() != null) {
      couponUsageRepository.save(CouponUsage.create(order.getCoupon(), order.getUser(), order));
    }
  }

  private OrderResponse cancel(Order order) {
    if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
      throw new OrderCancellationNotAllowedException();
    }
    if (order.getStatus() == OrderStatus.PAID) {
      for (OrderItem item : orderItemRepository.findByOrderId(order.getId())) {
        Product product = productRepository.findByIdForUpdate(item.getProduct().getId()).orElseThrow();
        product.returnStock(item.getQuantity());
      }
    }
    OrderStatus previous = order.getStatus();
    order.cancel();
    orderStatusHistoryRepository.save(
        OrderStatusHistory.record(order, previous, OrderStatus.CANCELLED, order.getUser().getEmail()));
    return toResponse(order);
  }

  private Coupon resolveCoupon(String code, UUID userId, BigDecimal subtotal) {
    if (code == null || code.isBlank()) {
      return null;
    }
    Coupon coupon =
        couponRepository
            .findByCode(code.trim().toUpperCase())
            .orElseThrow(() -> new InvalidCouponException("Coupon not found"));
    if (!Boolean.TRUE.equals(coupon.getActive()) || coupon.getExpiresAt().isBefore(Instant.now())) {
      throw new InvalidCouponException("Coupon is expired or inactive");
    }
    if (subtotal.compareTo(coupon.getMinimumOrderAmount()) < 0) {
      throw new InvalidCouponException("Order minimum amount was not reached for this coupon");
    }
    if (couponUsageRepository.existsByCouponIdAndUserId(coupon.getId(), userId)) {
      throw new InvalidCouponException("Coupon was already used by this user");
    }
    return coupon;
  }

  private BigDecimal calculateDiscount(Coupon coupon, BigDecimal subtotal) {
    if (coupon == null) {
      return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
    BigDecimal discount =
        coupon.getType() == CouponType.PERCENTAGE
            ? subtotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
            : coupon.getValue();
    return discount.min(subtotal).setScale(2, RoundingMode.HALF_UP);
  }

  private boolean isValidTransition(OrderStatus current, OrderStatus next) {
    return switch (current) {
      case PENDING -> next == OrderStatus.PAID || next == OrderStatus.CANCELLED;
      case PAID -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
      case SHIPPED -> next == OrderStatus.DELIVERED;
      case DELIVERED, CANCELLED -> false;
    };
  }

  private OrderResponse toResponse(Order order) {
    return toResponse(order, null);
  }

  private OrderResponse toResponse(Order order, String paymentUrl) {
    List<OrderItemResponse> items =
        orderItemRepository.findByOrderId(order.getId()).stream().map(OrderItemResponse::from).toList();
    return OrderResponse.from(order, items, paymentUrl);
  }

  private PaymentTransaction findTransactionByExternalReference(String externalReference) {
    if (externalReference == null || externalReference.isBlank()) {
      throw new OrderNotFoundException();
    }
    try {
      UUID orderId = UUID.fromString(externalReference);
      return paymentTransactionRepository.findByOrderIdOrderByCreatedAtDesc(orderId).stream()
          .findFirst()
          .orElseThrow(OrderNotFoundException::new);
    } catch (IllegalArgumentException exception) {
      throw new OrderNotFoundException();
    }
  }
}
