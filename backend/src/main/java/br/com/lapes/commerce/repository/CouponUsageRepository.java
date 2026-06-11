package br.com.lapes.commerce.repository;

import br.com.lapes.commerce.domain.CouponUsage;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, UUID> {

  boolean existsByCouponIdAndUserId(UUID couponId, UUID userId);

  boolean existsByCouponIdAndUserIdAndCancelledAtIsNull(UUID couponId, UUID userId);

  Optional<CouponUsage> findByCouponIdAndUserIdAndOrderId(UUID couponId, UUID userId, UUID orderId);
}
