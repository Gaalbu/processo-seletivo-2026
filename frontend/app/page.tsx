import styles from "./page.module.css";

export default function Home() {
  return (
    <main className={styles.main}>
      <section className={styles.panel}>
        <p className={styles.eyebrow}>&gt;_ booting storefront</p>
        <h1>&lt;LAPES.commerce /&gt;</h1>
        <p>
          Base web do e-commerce simplificado. Os fluxos de catálogo, carrinho,
          checkout e administração serão implementados nas próximas fases.
        </p>
      </section>
    </main>
  );
}
