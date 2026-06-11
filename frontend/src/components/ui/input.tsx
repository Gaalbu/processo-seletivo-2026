import type { InputHTMLAttributes } from "react";
import styles from "./input.module.css";

type InputProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string;
};

export function Input({ id, label, ...props }: InputProps) {
  return (
    <label className={styles.field} htmlFor={id}>
      <span>{label}</span>
      <input id={id} {...props} />
    </label>
  );
}
