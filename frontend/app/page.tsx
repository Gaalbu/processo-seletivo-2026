import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { ProductCard } from "../components/ui/product-card";
import styles from "./page.module.css";

const featuredProducts = [
  {
    name: "Teclado Mecânico Terminal Pro",
    category: "perifericos",
    price: "R$ 429,90",
    stock: 12,
    imageTone: "green" as const,
  },
  {
    name: "Monitor Dark Mode 27",
    category: "monitores",
    price: "R$ 1.699,00",
    stock: 6,
    imageTone: "blue" as const,
  },
  {
    name: "Headset Deploy Wireless",
    category: "audio",
    price: "R$ 549,90",
    stock: 9,
    imageTone: "purple" as const,
  },
];

export default function Home() {
  return (
    <main className={styles.main}>
      <div className={styles.hero}>
        <section className={styles.panel}>
          <span className={styles.eyebrow}>&gt;_ storefront online</span>
          <h1>&lt;LAPES.commerce /&gt;</h1>
          <p>
            Interface base do e-commerce com estética de editor e CLI: dark mode,
            cartões técnicos, ações diretas e feedbacks pensados para fluxos de compra rápidos.
          </p>

          <div className={styles.actions}>
            <Button variant="primary">Explorar catálogo</Button>
            <Button variant="ghost">Abrir command palette</Button>
          </div>

          <div className={styles.terminal}>
            <div className={styles.terminalBar}>
              <span>checkout.pipeline</span>
              <span>main</span>
            </div>
            <div className={styles.terminalBody}>
              <span>&gt; auth: <strong>jwt ready</strong></span>
              <span>&gt; catalog: <strong>redis cache enabled</strong></span>
              <span>&gt; checkout: <strong>stock lock active</strong></span>
            </div>
          </div>
        </section>

        <aside className={styles.side}>
          <section className={styles.statusPanel}>
            <span className={styles.label}>{"// order status"}</span>
            <h2>[PENDING] -- [PAID] -- [SHIPPED] -- [DELIVERED]</h2>
            <div className={styles.statusList}>
              <div className={styles.statusItem}><span>(✓) PENDING</span><strong className={styles.ok}>done</strong></div>
              <div className={styles.statusItem}><span>(•) PAID</span><strong className={styles.ok}>current</strong></div>
              <div className={styles.statusItem}><span>(○) SHIPPED</span><strong className={styles.pending}>queued</strong></div>
            </div>
          </section>

          <section className={styles.cartPreview}>
            <span className={styles.label}>{"// cart drawer"}</span>
            <h2>CART [2]</h2>
            <div className={styles.cartLine}>
              <div>[-] 1 [+]<span>Teclado Mecânico</span></div>
              <strong>R$ 429,90</strong>
            </div>
            <div className={styles.cartLine}>
              <div>[-] 1 [+]<span>Mouse Precision</span></div>
              <strong>R$ 189,90</strong>
            </div>
            <div className={styles.total}>TOTAL R$ 619,80</div>
          </section>
        </aside>

        <section className={styles.catalogPreview}>
          <div className={styles.sectionHeader}>
            <div>
              <span className={styles.label}>{"// product cards"}</span>
              <h2>Catálogo em modo terminal</h2>
            </div>
            <Badge>[ CACHE: REDIS ]</Badge>
          </div>
          {featuredProducts.map((product) => (
            <ProductCard key={product.name} {...product} />
          ))}
        </section>
      </div>
    </main>
  );
}
