package br.com.lapes.commerce.config;

import br.com.lapes.commerce.domain.Cart;
import br.com.lapes.commerce.domain.Coupon;
import br.com.lapes.commerce.domain.CouponType;
import br.com.lapes.commerce.domain.Product;
import br.com.lapes.commerce.domain.User;
import br.com.lapes.commerce.domain.UserRole;
import br.com.lapes.commerce.repository.CartRepository;
import br.com.lapes.commerce.repository.CouponRepository;
import br.com.lapes.commerce.repository.ProductRepository;
import br.com.lapes.commerce.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
@Order(1)
public class SeedDataLoader implements CommandLineRunner {

  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final CouponRepository couponRepository;
  private final CartRepository cartRepository;

  public SeedDataLoader(
      UserRepository userRepository,
      ProductRepository productRepository,
      CouponRepository couponRepository,
      CartRepository cartRepository) {
    this.userRepository = userRepository;
    this.productRepository = productRepository;
    this.couponRepository = couponRepository;
    this.cartRepository = cartRepository;
  }

  @Override
  public void run(String... args) {
    if (userRepository.count() > 0) {
      return;
    }

    User admin = User.customer("Admin LAPES", "admin@lapes.test", "$2a$10$3UsGNqmtVsKGSXIgbmVa8eVRKpLZNZ8gK5WgWvc7I9Gv8cuz1C5Oq");
    admin.setRole(UserRole.ADMIN);
    userRepository.save(admin);

    User customer = User.customer("Cliente LAPES", "cliente@lapes.test", "$2a$10$3UsGNqmtVsKGSXIgbmVa8eVRKpLZNZ8gK5WgWvc7I9Gv8cuz1C5Oq");
    userRepository.save(customer);

    cartRepository.save(Cart.forUser(customer));

    productRepository.save(Product.create("Teclado Mec\u00e2nico Terminal Pro", "Teclado mec\u00e2nico compacto com switches t\u00e1teis e ilumina\u00e7\u00e3o verde terminal.", new BigDecimal("429.90"), 12, "perifericos", "https://images.unsplash.com/photo-1587829741301-dc798b83add3"));
    productRepository.save(Product.create("Mouse Precision CLI", "Mouse ergon\u00f4mico de alta precis\u00e3o para setups de desenvolvimento.", new BigDecimal("189.90"), 25, "perifericos", "https://images.unsplash.com/photo-1527814050087-3793815479db"));
    productRepository.save(Product.create("Monitor Dark Mode 27", "Monitor QHD de 27 polegadas com alto contraste para longas sess\u00f5es de trabalho.", new BigDecimal("1699.00"), 6, "monitores", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf"));
    productRepository.save(Product.create("Headset Deploy Wireless", "Headset sem fio com microfone remov\u00edvel e bateria de longa dura\u00e7\u00e3o.", new BigDecimal("549.90"), 9, "audio", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e"));
    productRepository.save(Product.create("Dock Station Stack", "Dock USB-C com m\u00faltiplas portas para notebooks e workstations.", new BigDecimal("799.90"), 4, "acessorios", "https://images.unsplash.com/photo-1625842268584-8f3296236761"));

    couponRepository.save(Coupon.create("TERMINAL10", CouponType.PERCENTAGE, new BigDecimal("10.00"), new BigDecimal("100.00"), Instant.parse("2026-12-31T23:59:59Z"), true));
    couponRepository.save(Coupon.create("SHIP50", CouponType.FIXED_AMOUNT, new BigDecimal("50.00"), new BigDecimal("300.00"), Instant.parse("2026-12-31T23:59:59Z"), true));
    couponRepository.save(Coupon.create("EXPIRED20", CouponType.PERCENTAGE, new BigDecimal("20.00"), new BigDecimal("100.00"), Instant.parse("2025-01-01T00:00:00Z"), true));
  }
}
