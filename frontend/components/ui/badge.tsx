import type { ReactNode } from "react";
import styles from "./badge.module.css";

type BadgeProps = {
  children: ReactNode;
  tone?: "success" | "info" | "warning" | "danger";
};

export function Badge({ children, tone = "success" }: BadgeProps) {
  return <span className={`${styles.badge} ${styles[tone]}`}>{children}</span>;
}
