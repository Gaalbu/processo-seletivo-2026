import { apiRequest } from "./client";
import type { Product } from "./types";

export type ProductPayload = {
  name: string;
  description: string;
  price: number;
  stock: number;
  category: string;
  imageUrl: string;
};

export function createProduct(token: string, payload: ProductPayload) {
  return apiRequest<Product>("/admin/products", {
    body: JSON.stringify(payload),
    method: "POST",
    token,
  });
}

export function updateProduct(token: string, id: string, payload: ProductPayload) {
  return apiRequest<Product>(`/admin/products/${id}`, {
    body: JSON.stringify(payload),
    method: "PUT",
    token,
  });
}

export function deleteProduct(token: string, id: string) {
  return apiRequest<void>(`/admin/products/${id}`, { method: "DELETE", token });
}
