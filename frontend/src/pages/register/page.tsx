import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { register } from "../../lib/api/auth";
import { ApiError } from "../../lib/api/client";
import { useSessionStore } from "../../stores/session-store";
import { Button } from "../../components/ui/button";
import { Input } from "../../components/ui/input";
import styles from "../login/auth.module.css";

export function RegisterPage() {
  const navigate = useNavigate();
  const { setAuth } = useSessionStore();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState(">_ fill form");

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      const auth = await register({ name, email, password });
      setAuth(auth);
      navigate("/");
    } catch (error) {
      setMessage(error instanceof ApiError ? `>_ error_${error.status}: ${error.message}` : ">_ unexpected_error");
    }
  }

  return (
    <main className={styles.main}>
      <section className={styles.panel}>
        <h1>&gt;_ register</h1>
        <p>Crie uma conta customer. O backend cria o carrinho persistido automaticamente.</p>
        <form className={styles.form} onSubmit={handleSubmit}>
          <Input id="name" label="// name" value={name} onChange={(event) => setName(event.target.value)} />
          <Input id="email" label="// e-mail address" type="email" value={email} onChange={(event) => setEmail(event.target.value)} />
          <Input id="password" label="// password" type="password" value={password} onChange={(event) => setPassword(event.target.value)} />
          <Button type="submit">create customer</Button>
        </form>
        <div className={styles.links}>
          <Link to="/login">already registered</Link>
          <Link to="/">back catalog</Link>
        </div>
        <div className={styles.message}>{message}</div>
      </section>
    </main>
  );
}
