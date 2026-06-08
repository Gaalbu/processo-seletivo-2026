"use client";

import Link from "next/link";
import { useCart, useSession } from "../providers/app-providers";
import styles from "./app-header.module.css";

export function AppHeader() {
  const { user, logout } = useSession();
  const { cart } = useCart();
  const cartCount = cart?.items.reduce((total, item) => total + item.quantity, 0) ?? 0;

  return (
    <header className={styles.header}>
      <Link className={styles.logo} href="/" aria-label="LAPES Commerce home">
        &lt;LAPES.commerce/&gt;
      </Link>
      <div className={styles.command}>
        <span>&gt;_</span>
        <span>Buscar produtos, cupons, pedidos...</span>
        <kbd>Ctrl K</kbd>
      </div>
      <nav className={styles.nav} aria-label="Principal">
        <Link href="/">CATALOG</Link>
        {user ? <Link href="/orders">ORDERS</Link> : null}
        {user?.role === "ADMIN" ? <Link href="/admin">ADMIN</Link> : null}
        {user ? <button onClick={logout}>LOGOUT</button> : <Link href="/login">LOGIN</Link>}
        <Link href="/#cart">CART [{cartCount}]</Link>
      </nav>
    </header>
  );
}
