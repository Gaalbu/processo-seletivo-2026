import { create } from "zustand";
import type { AuthResponse, AuthUser } from "../lib/api/types";
import { clearSession, loadSession, saveSession } from "../lib/session";

type SessionState = {
  token: string | null;
  user: AuthUser | null;
  setAuth: (auth: AuthResponse) => void;
  logout: () => void;
};

function initializeSession() {
  const session = loadSession();
  return {
    token: session?.token ?? null,
    user: session?.user ?? null,
  };
}

export const useSessionStore = create<SessionState>((set) => ({
  ...initializeSession(),

  setAuth: (auth: AuthResponse) => {
    saveSession(auth);
    set({ token: auth.token, user: auth.user });
  },

  logout: () => {
    clearSession();
    set({ token: null, user: null });
  },
}));
