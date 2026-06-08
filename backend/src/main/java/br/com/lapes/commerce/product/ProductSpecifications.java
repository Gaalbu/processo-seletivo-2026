package br.com.lapes.commerce.product;

import br.com.lapes.commerce.domain.Product;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecifications {

  private ProductSpecifications() {}

  public static Specification<Product> filters(
      String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
    return (root, query, criteriaBuilder) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

      if (name != null && !name.isBlank()) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%"));
      }

      if (category != null && !category.isBlank()) {
        predicates.add(criteriaBuilder.equal(root.get("category"), category.trim().toLowerCase()));
      }

      if (minPrice != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
      }

      if (maxPrice != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
      }

      return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    };
  }
}
