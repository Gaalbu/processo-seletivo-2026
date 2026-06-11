import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { cancelOrder, listOrders } from "../../lib/api/orders";
import type { OrderStatus } from "../../lib/api/types";
import { useSessionStore } from "../../stores/session-store";
import { Badge } from "../../components/ui/badge";
import { Button } from "../../components/ui/button";
import styles from "./orders.module.css";

const statuses: OrderStatus[] = ["PENDING", "PAID", "SHIPPED", "DELIVERED"];

export function OrdersPage() {
  const { token, user } = useSessionStore();
  const queryClient = useQueryClient();

  const { data: orders = [], isLoading } = useQuery({
    queryKey: ["orders"],
    queryFn: () => listOrders(token!),
    enabled: !!token,
  });

  const cancelMutation = useMutation({
    mutationFn: (id: string) => cancelOrder(token!, id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["orders"] });
    },
  });

  if (!user) {
    return (
      <main className={styles.main}>
        <section className={styles.header}>
          <h1>&gt;_ orders</h1>
          <p><Link to="/login">Faça login</Link> para ver seus pedidos.</p>
        </section>
      </main>
    );
  }

  const message = isLoading ? ">_ loading_orders" : `>_ orders_loaded: ${orders.length}`;

  return (
    <main className={styles.main}>
      <div className={styles.wrap}>
        <section className={styles.header}>
          <h1>&gt;_ orders</h1>
          <p>{message}</p>
        </section>

        {orders.length ? orders.map((order) => (
          <article className={styles.order} key={order.id}>
            <div className={styles.orderTop}>
              <div>
                <Badge tone={order.status === "CANCELLED" ? "danger" : "success"}>[{order.status}]</Badge>
                <p className={styles.muted}>#{order.id.slice(0, 8)} · {new Date(order.createdAt).toLocaleString("pt-BR")}</p>
              </div>
              <strong>{formatCurrency(order.totalAmount)}</strong>
            </div>
            <div className={styles.pipeline}>{pipeline(order.status)}</div>
            <div className={styles.items}>
              {order.items.map((item) => (
                <div className={styles.item} key={item.id}>
                  <div>{item.productName}<span>{item.quantity} x {formatCurrency(item.unitPrice)}</span></div>
                  <strong>{formatCurrency(item.lineTotal)}</strong>
                </div>
              ))}
            </div>
            <div className={styles.orderFooter}>
              <span className={styles.muted}>discount {formatCurrency(order.discountAmount)} · payment {order.paymentStatus}</span>
              {order.status === "PENDING" || order.status === "PAID" ? (
                <Button onClick={() => cancelMutation.mutate(order.id)} variant="danger">cancel</Button>
              ) : null}
            </div>
          </article>
        )) : <p className={styles.empty}>&gt;_ no_orders</p>}
      </div>
    </main>
  );
}

function pipeline(current: OrderStatus) {
  if (current === "CANCELLED") return "[CANCELLED]";
  return statuses.map((status) => status === current ? `[${status}]` : status).join(" -- ");
}

function formatCurrency(value: number) {
  return new Intl.NumberFormat("pt-BR", { currency: "BRL", style: "currency" }).format(value);
}
