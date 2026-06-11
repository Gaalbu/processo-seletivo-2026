package br.com.lapes.commerce.repository;

import br.com.lapes.commerce.domain.OrderStatusHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {
}
