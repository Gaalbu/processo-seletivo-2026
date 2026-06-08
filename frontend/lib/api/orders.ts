import { apiRequest } from "./client";
import type { Order, OrderStatus } from "./types";

export function checkout(token: string, couponCode: string, paymentApproved = true) {
  return apiRequest<Order>("/checkout", {
    body: JSON.stringify({ couponCode: couponCode || null, paymentApproved }),
    method: "POST",
    token,
  });
}

export function listOrders(token: string) {
  return apiRequest<Order[]>("/orders", { token });
}

export function cancelOrder(token: string, id: string) {
  return apiRequest<Order>(`/orders/${id}/cancel`, { method: "POST", token });
}

export function listAdminOrders(token: string) {
  return apiRequest<Order[]>("/admin/orders", { token });
}

export function updateOrderStatus(token: string, id: string, status: OrderStatus) {
  return apiRequest<Order>(`/admin/orders/${id}/status`, {
    body: JSON.stringify({ status }),
    method: "PUT",
    token,
  });
}
