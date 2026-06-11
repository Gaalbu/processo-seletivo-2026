import { apiRequest } from "./client";
import type { Product, ProductPage } from "./types";

export type ProductFilters = {
  name?: string;
  category?: string;
  minPrice?: string;
  maxPrice?: string;
  page?: number;
  size?: number;
};

export function listProducts(filters: ProductFilters = {}) {
  const params = new URLSearchParams();
  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== "") {
      params.set(key, String(value));
    }
  });

  const query = params.toString();
  return apiRequest<ProductPage>(`/products${query ? `?${query}` : ""}`);
}

export function getProduct(id: string) {
  return apiRequest<Product>(`/products/${id}`);
}
