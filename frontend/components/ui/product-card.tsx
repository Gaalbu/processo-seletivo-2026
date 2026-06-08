import { Badge } from "./badge";
import { Button } from "./button";
import styles from "./product-card.module.css";

type ProductCardProps = {
  name: string;
  category: string;
  price: string;
  stock: number;
  imageTone: "green" | "blue" | "purple";
};

export function ProductCard({ name, category, price, stock, imageTone }: ProductCardProps) {
  return (
    <article className={styles.card}>
      <div className={`${styles.image} ${styles[imageTone]}`}>
        <Badge>[ STOCK: {stock > 0 ? "OK" : "OUT"} ]</Badge>
      </div>
      <div className={styles.body}>
        <span>{category}</span>
        <h3>{name}</h3>
        <div className={styles.footer}>
          <strong>{price}</strong>
          <Button variant="ghost">add</Button>
        </div>
      </div>
    </article>
  );
}
