package br.com.lapes.commerce.repository;

import br.com.lapes.commerce.domain.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

  List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

  Optional<Order> findByIdAndUserId(UUID id, UUID userId);
}
