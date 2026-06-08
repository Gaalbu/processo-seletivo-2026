"use client";

import type { AuthResponse, AuthUser } from "./api/types";

const TOKEN_KEY = "lapes-commerce-token";
const USER_KEY = "lapes-commerce-user";

export type Session = {
  token: string;
  user: AuthUser;
};

export function saveSession(auth: AuthResponse) {
  window.localStorage.setItem(TOKEN_KEY, auth.token);
  window.localStorage.setItem(USER_KEY, JSON.stringify(auth.user));
}

export function loadSession(): Session | null {
  const token = window.localStorage.getItem(TOKEN_KEY);
  const user = window.localStorage.getItem(USER_KEY);
  if (!token || !user) {
    return null;
  }

  return {
    token,
    user: JSON.parse(user) as AuthUser,
  };
}

export function clearSession() {
  window.localStorage.removeItem(TOKEN_KEY);
  window.localStorage.removeItem(USER_KEY);
}
