import { Link } from "react-router-dom";
import { useCartStore } from "../../stores/cart-store";
import { useSessionStore } from "../../stores/session-store";
import styles from "./app-header.module.css";

export function AppHeader() {
  const { user, logout } = useSessionStore();
  const cart = useCartStore((state) => state.cart);
  const cartCount = cart?.items.reduce((total, item) => total + item.quantity, 0) ?? 0;

  return (
    <header className={styles.header}>
      <Link className={styles.logo} to="/" aria-label="LAPES Commerce home">
        &lt;LAPES.commerce/&gt;
      </Link>
      <div className={styles.command}>
        <span>&gt;_</span>
        <span>Buscar produtos, cupons, pedidos...</span>
        <kbd>Ctrl K</kbd>
      </div>
      <nav className={styles.nav} aria-label="Principal">
        <Link to="/">CATALOG</Link>
        {user ? <Link to="/orders">ORDERS</Link> : null}
        {user?.role === "ADMIN" ? <Link to="/admin">ADMIN</Link> : null}
        {user ? <button onClick={logout}>LOGOUT</button> : <Link to="/login">LOGIN</Link>}
        <a href="/#cart">CART [{cartCount}]</a>
      </nav>
    </header>
  );
}
