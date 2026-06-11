import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { FormEvent, useState } from "react";
import { Link } from "react-router-dom";
import { createProduct, deleteProduct } from "../../lib/api/admin-products";
import { listAdminOrders, updateOrderStatus } from "../../lib/api/orders";
import { listProducts } from "../../lib/api/products";
import type { OrderStatus } from "../../lib/api/types";
import { useSessionStore } from "../../stores/session-store";
import { Button } from "../../components/ui/button";
import { Input } from "../../components/ui/input";
import styles from "./admin.module.css";

const statuses: OrderStatus[] = ["PENDING", "PAID", "SHIPPED", "DELIVERED", "CANCELLED"];

export function AdminPage() {
  const { token, user } = useSessionStore();
  const queryClient = useQueryClient();
  const [message, setMessage] = useState(">_ admin_ready");
  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    stock: "",
    category: "",
    imageUrl: "https://example.com/image.png",
  });

  const { data: productPage } = useQuery({
    queryKey: ["products", { size: 20 }],
    queryFn: () => listProducts({ size: 20 }),
  });

  const { data: orders = [] } = useQuery({
    queryKey: ["admin-orders"],
    queryFn: () => listAdminOrders(token!),
    enabled: !!token,
  });

  const createMutation = useMutation({
    mutationFn: () =>
      createProduct(token!, {
        name: form.name,
        description: form.description,
        price: Number(form.price),
        stock: Number(form.stock),
        category: form.category,
        imageUrl: form.imageUrl,
      }),
    onSuccess: () => {
      setMessage(">_ product_created");
      setForm({ name: "", description: "", price: "", stock: "", category: "", imageUrl: "https://example.com/image.png" });
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
    onError: () => setMessage(">_ failed_to_create_product"),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => deleteProduct(token!, id),
    onSuccess: () => {
      setMessage(">_ product_deleted");
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
    onError: () => setMessage(">_ failed_to_delete_product"),
  });

  const statusMutation = useMutation({
    mutationFn: ({ id, status }: { id: string; status: OrderStatus }) =>
      updateOrderStatus(token!, id, status),
    onSuccess: () => {
      setMessage(">_ order_status_updated");
      queryClient.invalidateQueries({ queryKey: ["admin-orders"] });
    },
    onError: () => setMessage(">_ failed_to_update_order"),
  });

  const products = productPage?.content ?? [];

  if (user?.role !== "ADMIN") {
    return (
      <main className={styles.main}>
        <section className={styles.header}>
          <h1>&gt;_ admin</h1>
          <p><Link to="/login">Faça login como admin</Link> para acessar o painel.</p>
        </section>
      </main>
    );
  }

  function submitProduct(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    createMutation.mutate();
  }

  return (
    <main className={styles.main}>
      <div className={styles.wrap}>
        <section className={styles.header}>
          <h1>&gt;_ admin panel</h1>
          <p>{message}</p>
        </section>

        <div className={styles.grid}>
          <section className={styles.panel}>
            <h2>{"// create product"}</h2>
            <form className={styles.form} onSubmit={submitProduct}>
              <Input id="p-name" label="// name" value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} />
              <Input id="p-description" label="// description" value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} />
              <Input id="p-price" label="// price" type="number" step="0.01" value={form.price} onChange={(event) => setForm({ ...form, price: event.target.value })} />
              <Input id="p-stock" label="// stock" type="number" value={form.stock} onChange={(event) => setForm({ ...form, stock: event.target.value })} />
              <Input id="p-category" label="// category" value={form.category} onChange={(event) => setForm({ ...form, category: event.target.value })} />
              <Input id="p-image" label="// image url" value={form.imageUrl} onChange={(event) => setForm({ ...form, imageUrl: event.target.value })} />
              <Button type="submit">create product</Button>
            </form>
          </section>

          <section className={styles.panel}>
            <h2>{"// products"}</h2>
            <div className={styles.list}>
              {products.map((product) => (
                <div className={styles.row} key={product.id}>
                  <div><strong>{product.name}</strong><span>{product.category} · {formatCurrency(product.price)} · stock {product.stock}</span></div>
                  <Button onClick={() => deleteMutation.mutate(product.id)} variant="danger">delete</Button>
                </div>
              ))}
            </div>
          </section>
        </div>

        <section className={styles.panel}>
          <h2>{"// orders"}</h2>
          <div className={styles.list}>
            {orders.map((order) => (
              <div className={styles.row} key={order.id}>
                <div><strong>#{order.id.slice(0, 8)} · {formatCurrency(order.totalAmount)}</strong><span>{order.status} · payment {order.paymentStatus}</span></div>
                <div className={styles.actions}>
                  <select className={styles.select} value={order.status} onChange={(event) => statusMutation.mutate({ id: order.id, status: event.target.value as OrderStatus })}>
                    {statuses.map((status) => <option key={status} value={status}>{status}</option>)}
                  </select>
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </main>
  );
}

function formatCurrency(value: number) {
  return new Intl.NumberFormat("pt-BR", { currency: "BRL", style: "currency" }).format(value);
}
