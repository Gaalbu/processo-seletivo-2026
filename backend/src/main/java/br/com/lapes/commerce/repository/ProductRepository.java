package br.com.lapes.commerce.repository;

import br.com.lapes.commerce.domain.Product;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Product p where p.id = :id and p.deletedAt is null")
  Optional<Product> findActiveByIdForUpdate(UUID id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Product p where p.id = :id")
  Optional<Product> findByIdForUpdate(UUID id);

  @Query("select p.deletedAt from Product p where p.id = :id")
  Instant findDeletedAtById(UUID id);

  @Modifying
  @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :id AND p.stock + :quantity >= 0")
  int incrementStock(@Param("id") UUID id, @Param("quantity") int quantity);
}
