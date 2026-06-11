import { create } from "zustand";
import { getCart } from "../lib/api/cart";
import type { Cart } from "../lib/api/types";

type CartState = {
  cart: Cart | null;
  refreshCart: (token: string) => Promise<void>;
  clearLocalCart: () => void;
};

export const useCartStore = create<CartState>((set) => ({
  cart: null,

  refreshCart: async (token: string) => {
    try {
      set({ cart: await getCart(token) });
    } catch {
      set({ cart: null });
    }
  },

  clearLocalCart: () => {
    set({ cart: null });
  },
}));
