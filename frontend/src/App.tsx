import { useEffect } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { AppHeader } from "./components/layout/app-header";
import { Storefront } from "./pages/storefront/storefront";
import { LoginPage } from "./pages/login/page";
import { RegisterPage } from "./pages/register/page";
import { OrdersPage } from "./pages/orders/page";
import { AdminPage } from "./pages/admin/page";
import { useCartStore } from "./stores/cart-store";
import { useSessionStore } from "./stores/session-store";

function CartLoader() {
  const { token, user } = useSessionStore();
  const refreshCart = useCartStore((state) => state.refreshCart);

  useEffect(() => {
    if (token && user?.role === "CUSTOMER") {
      refreshCart(token);
    }
  }, [token, user?.role, refreshCart]);

  return null;
}

export function App() {
  return (
    <BrowserRouter>
      <CartLoader />
      <AppHeader />
      <Routes>
        <Route path="/" element={<Storefront />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/orders" element={<OrdersPage />} />
        <Route path="/admin" element={<AdminPage />} />
      </Routes>
    </BrowserRouter>
  );
}
