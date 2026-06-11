import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { login } from "../../lib/api/auth";
import { ApiError } from "../../lib/api/client";
import { useSessionStore } from "../../stores/session-store";
import { Button } from "../../components/ui/button";
import { Input } from "../../components/ui/input";
import styles from "./auth.module.css";

export function LoginPage() {
  const navigate = useNavigate();
  const { setAuth } = useSessionStore();
  const [email, setEmail] = useState("cliente@lapes.test");
  const [password, setPassword] = useState("password123");
  const [message, setMessage] = useState(">_ ready");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      const auth = await login({ email, password });
      setAuth(auth);
      navigate(auth.user.role === "ADMIN" ? "/admin" : "/");
    } catch (error) {
      setMessage(error instanceof ApiError ? `>_ error_${error.status}: ${error.message}` : ">_ unexpected_error");
    }
  }

  return (
    <main className={styles.main}>
      <section className={styles.panel}>
        <h1>&gt;_ login</h1>
        <p>Entre como cliente ou admin para acessar carrinho, pedidos e painel administrativo.</p>
        <form className={styles.form} onSubmit={handleSubmit}>
          <Input id="email" label="// e-mail address" type="email" value={email} onChange={(event) => setEmail(event.target.value)} />
          <Input id="password" label="// password" type="password" value={password} onChange={(event) => setPassword(event.target.value)} />
          <Button type="submit">authenticate</Button>
        </form>
        <div className={styles.links}>
          <Link to="/register">create account</Link>
          <Link to="/">back catalog</Link>
        </div>
        <div className={styles.message}>{message}</div>
      </section>
    </main>
  );
}
