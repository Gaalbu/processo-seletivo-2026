import { apiRequest } from "./client";
import type { AuthResponse, AuthUser } from "./types";

export type LoginPayload = {
  email: string;
  password: string;
};

export type RegisterPayload = LoginPayload & {
  name: string;
};

export function login(payload: LoginPayload) {
  return apiRequest<AuthResponse>("/auth/login", {
    body: JSON.stringify(payload),
    method: "POST",
  });
}

export function register(payload: RegisterPayload) {
  return apiRequest<AuthResponse>("/auth/register", {
    body: JSON.stringify(payload),
    method: "POST",
  });
}

export function me(token: string) {
  return apiRequest<AuthUser>("/auth/me", { token });
}
