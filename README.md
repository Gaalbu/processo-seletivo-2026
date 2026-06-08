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
- Swagger UI: `http://localhost:8080/swagger-ui.html`

Endpoints de autenticacao:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/admin/ping`, restrito a `ADMIN`

Endpoints de catalogo:

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/admin/products`, restrito a `ADMIN`
- `PUT /api/admin/products/{id}`, restrito a `ADMIN`
- `DELETE /api/admin/products/{id}`, restrito a `ADMIN`

Filtros de catalogo:

- `name`
- `category`
- `minPrice`
- `maxPrice`
- `page`
- `size`

Endpoints de carrinho:

- `GET /api/cart`, restrito a `CUSTOMER`
- `POST /api/cart/items`, restrito a `CUSTOMER`
- `PUT /api/cart/items/{itemId}`, restrito a `CUSTOMER`
- `DELETE /api/cart/items/{itemId}`, restrito a `CUSTOMER`
- `DELETE /api/cart`, restrito a `CUSTOMER`

Endpoints de checkout e pedidos:

- `POST /api/checkout`, restrito a `CUSTOMER`
- `GET /api/orders`, restrito a `CUSTOMER`
- `GET /api/orders/{id}`, restrito a `CUSTOMER`
- `POST /api/orders/{id}/cancel`, restrito a `CUSTOMER`
- `GET /api/admin/orders`, restrito a `ADMIN`
- `PUT /api/admin/orders/{id}/status`, restrito a `ADMIN`
- `POST /api/admin/orders/{id}/cancel`, restrito a `ADMIN`

Checkout:

- `couponCode`: codigo opcional de cupom.
- `paymentApproved`: `true` ou omitido simula pagamento aprovado; `false` simula falha de pagamento.

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
- Catalogo publico com listagem, detalhe, filtros e paginacao.
- CRUD administrativo de produtos com soft delete.
- Cache Redis para listagem/detalhe de produtos, com invalidacao em criacao, edicao e remocao.
- Rate limiting em memoria para endpoints publicos do catalogo.
- Swagger/OpenAPI inicial com endpoints de autenticacao e catalogo.
- Carrinho persistido por usuario autenticado.
- Operacoes de adicionar item, remover item, atualizar quantidade e limpar carrinho.
- Validacao de estoque ao adicionar e atualizar quantidade.
- Respostas padronizadas para carrinho inexistente, item inexistente e estoque insuficiente.
- Checkout transacional com lock pessimista nos produtos para evitar overselling.
- Calculo do total com snapshot de preco no momento do checkout.
- Aplicacao de cupons percentuais ou de valor fixo.
- Validacao de cupom expirado/inativo, uso unico por usuario e valor minimo do pedido.
- Simulacao de pagamento aprovado ou falho.
- Criacao de pedidos com itens historicos e estados `PENDING`, `PAID`, `SHIPPED`, `DELIVERED` e `CANCELLED`.
- Cancelamento antes de envio com devolucao de estoque quando o pedido estava pago.
- Administracao basica de pedidos com listagem, atualizacao de status e cancelamento.
- Testes automatizados dos fluxos criticos de checkout aprovado, falha de pagamento, cupons e concorrencia no ultimo item em estoque.

## Testes

```bash
cd backend
mvn test
```

Cobertura critica atual:

- Checkout aprovado reserva estoque, limpa carrinho e preserva snapshot de preco.
- Falha de pagamento nao reserva estoque e nao limpa carrinho.
- Cupom percentual calcula desconto e registra uso unico.
- Cupom expirado e rejeitado.
- Cupom com valor minimo nao atingido e rejeitado.
- Cupom ja usado pelo usuario e rejeitado.
- Duas tentativas simultaneas de checkout para o ultimo item permitem apenas uma compra.

As proximas fases implementarao autenticacao, catalogo, carrinho, checkout, cupons, testes criticos e documentacao final.
