package br.com.lapes.commerce.product;

import java.io.Serializable;
import java.util.List;

public record ProductPageResponse(
    List<ProductResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last)
    implements Serializable {}
