import { BrowserRouter, Route, Routes } from "react-router-dom";
import { AppHeader } from "./components/layout/app-header";
import { Storefront } from "./pages/storefront/storefront";
import { LoginPage } from "./pages/login/page";
import { RegisterPage } from "./pages/register/page";
import { OrdersPage } from "./pages/orders/page";
import { AdminPage } from "./pages/admin/page";

export function App() {
  return (
    <BrowserRouter>
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
