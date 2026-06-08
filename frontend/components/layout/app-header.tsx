import Link from "next/link";
import styles from "./app-header.module.css";

export function AppHeader() {
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
        <Link href="/">LOGIN</Link>
        <Link href="/">CART [0]</Link>
      </nav>
    </header>
  );
}
