"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import { cancelOrder, listOrders } from "../../lib/api/orders";
import type { Order, OrderStatus } from "../../lib/api/types";
import { useSession } from "../../components/providers/app-providers";
import { Badge } from "../../components/ui/badge";
import { Button } from "../../components/ui/button";
import styles from "./orders.module.css";

const statuses: OrderStatus[] = ["PENDING", "PAID", "SHIPPED", "DELIVERED"];

export default function OrdersPage() {
  const { token, user } = useSession();
  const [orders, setOrders] = useState<Order[]>([]);
  const [message, setMessage] = useState(">_ loading_orders");

  const load = useCallback(async () => {
    if (!token) return;
    try {
      const data = await listOrders(token);
      setOrders(data);
      setMessage(`>_ orders_loaded: ${data.length}`);
    } catch {
      setMessage(">_ failed_to_load_orders");
    }
  }, [token]);

  useEffect(() => {
    void load();
  }, [load]);

  async function cancel(id: string) {
    if (!token) return;
    await cancelOrder(token, id);
    await load();
  }

  if (!user) {
    return <main className={styles.main}><section className={styles.header}><h1>&gt;_ orders</h1><p><Link href="/login">Faça login</Link> para ver seus pedidos.</p></section></main>;
  }

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
              {order.status === "PENDING" || order.status === "PAID" ? <Button onClick={() => cancel(order.id)} variant="danger">cancel</Button> : null}
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
