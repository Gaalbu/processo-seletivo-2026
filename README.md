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

## Como Rodar O Frontend

```bash
cd frontend
npm install
npm run dev
```

Web local:

- `http://localhost:3000`

## Status Da Implementacao

Fase atual: base do projeto.

Entregue nesta fase:

- Estrutura inicial de monorepo com `backend/` e `frontend/`.
- Aplicacao Spring Boot inicial com endpoint de health.
- Aplicacao Next.js inicial com tema dark inspirado na estetica terminal.
- Docker Compose com PostgreSQL e Redis.
- CI basico com build/testes de backend e build de frontend.

As proximas fases implementarao modelo de dados, autenticacao, catalogo, carrinho, checkout, cupons, testes criticos e documentacao final.
