import { apiRequest } from "./client";
import type { Cart } from "./types";

export function getCart(token: string) {
  return apiRequest<Cart>("/cart", { token });
}

export function addCartItem(token: string, productId: string, quantity: number) {
  return apiRequest<Cart>("/cart/items", {
    body: JSON.stringify({ productId, quantity }),
    method: "POST",
    token,
  });
}

export function updateCartItem(token: string, itemId: string, quantity: number) {
  return apiRequest<Cart>(`/cart/items/${itemId}`, {
    body: JSON.stringify({ quantity }),
    method: "PUT",
    token,
  });
}

export function removeCartItem(token: string, itemId: string) {
  return apiRequest<void>(`/cart/items/${itemId}`, {
    method: "DELETE",
    token,
  });
}
