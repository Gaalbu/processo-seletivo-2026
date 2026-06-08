package br.com.lapes.commerce.repository;

import br.com.lapes.commerce.domain.CartItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {}
