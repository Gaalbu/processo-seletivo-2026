package br.com.lapes.commerce.repository;

import br.com.lapes.commerce.domain.PaymentTransaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

  Optional<PaymentTransaction> findByProviderAndProviderPaymentId(String provider, String providerPaymentId);

  List<PaymentTransaction> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
