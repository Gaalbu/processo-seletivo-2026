import { useMutation, useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { addCartItem, clearCart, removeCartItem, updateCartItem } from "../../lib/api/cart";
import { ApiError } from "../../lib/api/client";
import { checkout } from "../../lib/api/orders";
import { listProducts } from "../../lib/api/products";
import type { ProductFilters } from "../../lib/api/products";
import { useCartStore } from "../../stores/cart-store";
import { useSessionStore } from "../../stores/session-store";
import { Badge } from "../../components/ui/badge";
import { Button } from "../../components/ui/button";
import { Input } from "../../components/ui/input";
import styles from "./storefront.module.css";

export function Storefront() {
  const { token, user } = useSessionStore();
  const { cart, refreshCart } = useCartStore();
  const [name, setName] = useState("");
  const [category, setCategory] = useState("");
  const [couponCode, setCouponCode] = useState("");
  const [message, setMessage] = useState("Carregando catálogo...");

  const filters: ProductFilters = { name, category, size: 12 };

  const { data, isLoading, refetch } = useQuery({
    queryKey: ["products", filters],
    queryFn: () => listProducts(filters),
  });

  const addMutation = useMutation({
    mutationFn: ({ productId }: { productId: string }) => addCartItem(token!, productId, 1),
    onSuccess: () => {
      refreshCart(token!);
      setMessage("product_added");
    },
    onError: (error) => setMessage(errorMessage(error)),
  });

  const updateMutation = useMutation({
    mutationFn: async ({ itemId, quantity }: { itemId: string; quantity: number }) => {
      if (quantity <= 0) {
        await removeCartItem(token!, itemId);
        return null;
      }
      return updateCartItem(token!, itemId, quantity);
    },
    onSuccess: () => {
      refreshCart(token!);
    },
    onError: (error) => setMessage(errorMessage(error)),
  });

  const clearMutation = useMutation({
    mutationFn: () => clearCart(token!),
    onSuccess: () => {
      refreshCart(token!);
      setMessage("cart_cleared");
    },
    onError: (error) => setMessage(errorMessage(error)),
  });

  const checkoutMutation = useMutation({
    mutationFn: ({ couponCode, paymentApproved }: { couponCode: string; paymentApproved?: boolean }) =>
      checkout(token!, couponCode, paymentApproved),
    onSuccess: (order) => {
      refreshCart(token!);
      if (order.paymentUrl) {
        setMessage("payment_redirect: abrindo gateway");
        window.location.href = order.paymentUrl;
        return;
      }
      setMessage(`checkout_${order.paymentStatus.toLowerCase()}: ${formatCurrency(order.totalAmount)}`);
    },
    onError: (error) => setMessage(errorMessage(error)),
  });

  const products = data?.content ?? [];
  const isLoadingProducts = isLoading;

  async function addToCart(productId: string) {
    if (!token || user?.role !== "CUSTOMER") {
      setMessage("auth_required: faça login como cliente");
      return;
    }
    addMutation.mutate({ productId });
  }

  function changeQuantity(itemId: string, quantity: number) {
    updateMutation.mutate({ itemId, quantity });
  }

  function finishCheckout(paymentApproved?: boolean) {
    if (!token || user?.role !== "CUSTOMER") {
      setMessage("auth_required: faça login como cliente");
      return;
    }
    checkoutMutation.mutate({ couponCode, paymentApproved });
  }

  return (
    <main className={styles.main}>
      <section className={styles.hero}>
        <div>
          <span className={styles.eyebrow}>&gt;_ storefront online</span>
          <h1>&lt;LAPES.commerce /&gt;</h1>
          <p>Catálogo, carrinho e checkout consumindo a API Spring Boot.</p>
        </div>
        <div className={styles.console}>&gt;_ {message}</div>
      </section>

      <section className={styles.filters}>
        <Input id="name" label="// product name" value={name} onChange={(event) => setName(event.target.value)} />
        <Input id="category" label="// category" value={category} onChange={(event) => setCategory(event.target.value)} />
        <Button onClick={() => refetch()} variant="ghost">{isLoadingProducts ? "loading" : "run search"}</Button>
      </section>

      <div className={styles.grid}>
        <section className={styles.catalog}>
          {products.map((product) => (
            <article className={styles.product} key={product.id}>
              <div className={styles.productImage}>
                <Badge tone={product.stock > 0 ? "success" : "danger"}>[ STOCK: {product.stock} ]</Badge>
              </div>
              <div className={styles.productBody}>
                <span>{product.category}</span>
                <h2>{product.name}</h2>
                <p>{product.description}</p>
                <div className={styles.productFooter}>
                  <strong>{formatCurrency(product.price)}</strong>
                  <Button onClick={() => addToCart(product.id)} variant="ghost">add</Button>
                </div>
              </div>
            </article>
          ))}
        </section>

        <aside className={styles.cart} id="cart">
          <div className={styles.cartHeader}>
            <div>
              <span className={styles.eyebrow}>{"// persistent cart"}</span>
              <h2>CART [{cart?.items.length ?? 0}]</h2>
            </div>
            <Button onClick={() => clearMutation.mutate()} variant="danger">clear</Button>
          </div>

          <div className={styles.cartItems}>
            {cart?.items.length ? cart.items.map((item) => (
              <div className={styles.cartItem} key={item.id}>
                <div>
                  <strong>{item.productName}</strong>
                  <span>{formatCurrency(item.unitPrice)} · stock {item.availableStock}</span>
                </div>
                <div className={styles.quantity}>
                  <button onClick={() => changeQuantity(item.id, item.quantity - 1)}>-</button>
                  <span>{item.quantity}</span>
                  <button onClick={() => changeQuantity(item.id, item.quantity + 1)}>+</button>
                </div>
              </div>
            )) : <p className={styles.empty}>&gt;_ cart_empty</p>}
          </div>

          <Input id="coupon" label="// coupon code" value={couponCode} onChange={(event) => setCouponCode(event.target.value)} />
          <div className={styles.total}>TOTAL {formatCurrency(cart?.subtotal ?? 0)}</div>
          <div className={styles.checkoutActions}>
            <Button onClick={() => finishCheckout()}>pay now</Button>
            <Button onClick={() => finishCheckout(false)} variant="ghost">simulate fail</Button>
          </div>
        </aside>
      </div>
    </main>
  );
}

function formatCurrency(value: number) {
  return new Intl.NumberFormat("pt-BR", { currency: "BRL", style: "currency" }).format(value);
}

function errorMessage(error: unknown) {
  return error instanceof ApiError ? `error_${error.status}: ${error.message}` : "unexpected_error";
}
