"use client";

import { createContext, useCallback, useContext, useEffect, useState, type ReactNode } from "react";
import { getCart as fetchCart } from "../../lib/api/cart";
import type { AuthResponse, AuthUser, Cart } from "../../lib/api/types";
import { clearSession, loadSession, saveSession, type Session } from "../../lib/session";

type SessionContextValue = {
  token: string | null;
  user: AuthUser | null;
  setAuth: (auth: AuthResponse) => void;
  logout: () => void;
};

type CartContextValue = {
  cart: Cart | null;
  refreshCart: () => Promise<void>;
  clearLocalCart: () => void;
};

const SessionContext = createContext<SessionContextValue | null>(null);
const CartContext = createContext<CartContextValue | null>(null);

export function AppProviders({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(() => {
    if (typeof window === "undefined") {
      return null;
    }
    return loadSession();
  });
  const [cart, setCart] = useState<Cart | null>(null);
  const token = session?.token ?? null;
  const user = session?.user ?? null;

  const refreshCart = useCallback(async () => {
    if (!token || user?.role !== "CUSTOMER") {
      setCart(null);
      return;
    }

    setCart(await fetchCart(token));
  }, [token, user?.role]);

  useEffect(() => {
    async function loadCart() {
      await refreshCart();
    }

    void loadCart();
  }, [refreshCart]);

  function setAuth(auth: AuthResponse) {
    saveSession(auth);
    setSession({ token: auth.token, user: auth.user });
  }

  function logout() {
    clearSession();
    setSession(null);
    setCart(null);
  }

  return (
    <SessionContext.Provider value={{ token, user, setAuth, logout }}>
      <CartContext.Provider value={{ cart, refreshCart, clearLocalCart: () => setCart(null) }}>
        {children}
      </CartContext.Provider>
    </SessionContext.Provider>
  );
}

export function useSession() {
  const context = useContext(SessionContext);
  if (!context) {
    throw new Error("useSession must be used inside AppProviders");
  }
  return context;
}

export function useCart() {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error("useCart must be used inside AppProviders");
  }
  return context;
}
