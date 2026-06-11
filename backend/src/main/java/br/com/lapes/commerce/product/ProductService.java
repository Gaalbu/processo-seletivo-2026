package br.com.lapes.commerce.product;

import br.com.lapes.commerce.domain.Product;
import br.com.lapes.commerce.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

  private static final int MAX_PAGE_SIZE = 100;

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Cacheable(
      cacheNames = "products:list",
      key = "T(java.util.Objects).hash(#name, #category, #minPrice, #maxPrice, #page, #size)")
  public ProductPageResponse list(
      String name, String category, BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
    int safePage = Math.max(page, 0);
    int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    var pageable = PageRequest.of(safePage, safeSize, Sort.by("createdAt").descending());
    var products =
        productRepository.findAll(
            ProductSpecifications.filters(name, category, minPrice, maxPrice), pageable);

    return new ProductPageResponse(
        products.map(ProductResponse::from).toList(),
        products.getNumber(),
        products.getSize(),
        products.getTotalElements(),
        products.getTotalPages(),
        products.isLast());
  }

  @Cacheable(cacheNames = "products:detail", key = "#id")
  public ProductResponse detail(UUID id) {
    Product product = activeProduct(id);
    return ProductResponse.from(product);
  }

  @Transactional
  @CacheEvict(cacheNames = {"products:list", "products:detail"}, allEntries = true)
  public ProductResponse create(ProductRequest request) {
    Product product =
        Product.create(
            request.name().trim(),
            request.description().trim(),
            request.price(),
            request.stock(),
            request.category().trim().toLowerCase(),
            request.imageUrl().trim());
    return ProductResponse.from(productRepository.save(product));
  }

  @Transactional
  @CacheEvict(cacheNames = {"products:list", "products:detail"}, allEntries = true)
  public ProductResponse update(UUID id, ProductRequest request) {
    Product product = activeProduct(id);
    product.update(
        request.name().trim(),
        request.description().trim(),
        request.price(),
        request.stock(),
        request.category().trim().toLowerCase(),
        request.imageUrl().trim());
    return ProductResponse.from(product);
  }

  @Transactional
  @CacheEvict(cacheNames = {"products:list", "products:detail"}, allEntries = true)
  public ProductResponse updateStock(UUID id, StockRequest request) {
    Product product = activeProduct(id);
    product.adjustStock(request.quantity());
    return ProductResponse.from(productRepository.save(product));
  }

  @Transactional
  @CacheEvict(cacheNames = {"products:list", "products:detail"}, allEntries = true)
  public void delete(UUID id) {
    activeProduct(id).softDelete();
  }

  private Product activeProduct(UUID id) {
    Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
    if (product.getDeletedAt() != null) {
      throw new ProductNotFoundException();
    }
    return product;
  }
}
