import type { Metadata } from "next";
import type { ReactNode } from "react";
import { AppHeader } from "../components/layout/app-header";
import { AppProviders } from "../components/providers/app-providers";
import "./globals.css";

export const metadata: Metadata = {
  title: "LAPES Commerce",
  description: "Sistema e-commerce simplificado para o processo seletivo LAPES 2026.",
};

export default function RootLayout({ children }: Readonly<{ children: ReactNode }>) {
  return (
    <html lang="pt-BR">
      <body>
        <AppProviders>
          <AppHeader />
          {children}
        </AppProviders>
      </body>
    </html>
  );
}
