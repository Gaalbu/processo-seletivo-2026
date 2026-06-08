# LAPES Commerce

Nome: Gabriel Albuquerque Alencar  
Trilha escolhida: Trilha de Desenvolvimento  
Contato: gabriel24070064@aluno.cesupa.br / telefone: +5591984053439

## Visao Geral

Sistema e-commerce simplificado desenvolvido para o Processo Seletivo LAPES 2026.

O projeto sera entregue como uma aplicacao fullstack com API backend e interface web. A implementacao sera feita em fases para manter um historico de commits limpo e facilitar revisoes incrementais.

## Stack

- Backend: Java 21, Spring Boot 3, Maven
- Frontend: Next.js, React, TypeScript
- Banco de dados: PostgreSQL
- Cache: Redis
- Infra local: Docker Compose
- CI: GitHub Actions

## Estrutura

```text
backend/   API Java com Spring Boot
frontend/  Interface web com Next.js
dev/       Enunciado e referencias visuais do desafio
```

## Como Rodar A Infra Local

```bash
docker compose up -d
```

Servicos locais:

- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`

## Como Rodar O Backend

```bash
cd backend
mvn spring-boot:run
```

API local:

- Health: `http://localhost:8080/api/health`
- Actuator health: `http://localhost:8080/actuator/health`

Endpoints de autenticacao:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/admin/ping`, restrito a `ADMIN`

Usuarios seedados:

- Admin: `admin@lapes.test` / `password123`
- Cliente: `cliente@lapes.test` / `password123`

## Como Rodar O Frontend

```bash
cd frontend
npm install
npm run dev
```

Web local:

- `http://localhost:3000`

## Status Da Implementacao

Fase atual: base de dados e modelo de dominio.

Entregue nesta fase:

- Estrutura inicial de monorepo com `backend/` e `frontend/`.
- Aplicacao Spring Boot inicial com endpoint de health.
- Aplicacao Next.js inicial com tema dark inspirado na estetica terminal.
- Docker Compose com PostgreSQL e Redis.
- CI basico com build/testes de backend e build de frontend.
- Dependencias de persistencia com Spring Data JPA, PostgreSQL, Redis e Flyway.
- Entidades e enums dos dominios de usuarios, produtos, carrinho, pedidos e cupons.
- Repositorios Spring Data para os agregados principais.
- Migrations Flyway para criacao do schema inicial.
- Seed inicial com usuarios, carrinho, produtos e cupons de exemplo.
- Configuracao de teste com H2 para permitir `mvn test` sem depender de Docker local.
- Autenticacao com registro, login, JWT e hash BCrypt de senha.
- Protecao stateless de rotas com roles `ADMIN` e `CUSTOMER`.
- Rate limiting em memoria para login e registro.
- Respostas padronizadas de erro para validacao, credenciais invalidas, 401, 403 e 429.

As proximas fases implementarao autenticacao, catalogo, carrinho, checkout, cupons, testes criticos e documentacao final.
