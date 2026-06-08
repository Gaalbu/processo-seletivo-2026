package br.com.lapes.commerce.repository;

import br.com.lapes.commerce.domain.Product;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {}
