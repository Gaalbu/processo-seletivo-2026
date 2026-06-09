# LAPES Commerce

Nome: Gabriel Albuquerque Alencar  
Trilha escolhida: Trilha de Desenvolvimento  
Contato: gabriel24070064@aluno.cesupa.br / telefone: +5591984053439

## Visao Geral

Sistema e-commerce fullstack desenvolvido para o Processo Seletivo LAPES 2026.

O projeto implementa API backend e interface web para autenticacao, catalogo de produtos, carrinho persistido, checkout transacional, pedidos e cupons de desconto.

## Stack

- Backend: Java 21, Spring Boot 3, Maven
- Frontend: Next.js, React, TypeScript
- Banco de dados: PostgreSQL
- Cache externo: Redis
- Migrations: Flyway
- Testes: JUnit 5, Spring Boot Test, H2 em ambiente local de teste
- API docs: Swagger/OpenAPI via springdoc
- CI: GitHub Actions
- Infra local: Docker Compose

## Estrutura

```text
backend/   API Java com Spring Boot
frontend/  Interface web com Next.js
dev/       Enunciado e referencia visual do desafio
```

## Setup Do Zero

Requisitos:

- Java 21+
- Maven 3.9+
- Node.js 22+
- npm
- Docker e Docker Compose para PostgreSQL e Redis

Subir infraestrutura local:

```bash
docker compose up -d
```

Servicos:

- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`

Rodar backend:

```bash
cd backend
mvn spring-boot:run
```

Rodar frontend:

```bash
cd frontend
npm install
npm run dev
```

URLs:

- Web: `http://localhost:3000`
- API health: `http://localhost:8080/api/health`
- Actuator health: `http://localhost:8080/actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Variaveis De Ambiente

Backend, exemplo em `backend/.env.example`:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/lapes_commerce
SPRING_DATASOURCE_USERNAME=lapes
SPRING_DATASOURCE_PASSWORD=lapes
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
APP_JWT_SECRET=change-me-with-at-least-32-characters-in-production
APP_JWT_EXPIRATION_MINUTES=120
APP_RATE_LIMIT_AUTH_CAPACITY=10
APP_RATE_LIMIT_AUTH_REFILL_WINDOW_SECONDS=60
APP_RATE_LIMIT_CATALOG_CAPACITY=60
APP_RATE_LIMIT_CATALOG_REFILL_WINDOW_SECONDS=60
```

Frontend, exemplo em `frontend/.env.example`:

```text
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

## Seed

O Flyway cria o schema e popula dados iniciais automaticamente ao iniciar o backend com PostgreSQL.

Usuarios seedados:

- Admin: `admin@lapes.test` / `password123`
- Cliente: `cliente@lapes.test` / `password123`

Cupons seedados:

- `TERMINAL10`: 10% de desconto, minimo R$ 100,00
- `SHIP50`: R$ 50,00 de desconto, minimo R$ 300,00
- `EXPIRED20`: cupom expirado para validar regra de expiracao

## Interface Web

Rotas principais:

- `/`: catalogo, filtros, carrinho e checkout.
- `/login`: autenticacao com JWT.
- `/register`: cadastro de cliente.
- `/orders`: pedidos do cliente autenticado.
- `/admin`: painel administrativo para usuario `ADMIN`.

Estetica adotada:

- Dark mode first.
- Visual inspirado em terminal, editor de codigo e CLI.
- Cards com badges `[ STOCK: OK ]`.
- Carrinho no formato de linhas de execucao.
- Status de pedido no formato de pipeline.

## API

Autenticacao:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

Catalogo:

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

Carrinho:

- `GET /api/cart`, restrito a `CUSTOMER`
- `POST /api/cart/items`, restrito a `CUSTOMER`
- `PUT /api/cart/items/{itemId}`, restrito a `CUSTOMER`
- `DELETE /api/cart/items/{itemId}`, restrito a `CUSTOMER`
- `DELETE /api/cart`, restrito a `CUSTOMER`

Checkout e pedidos:

- `POST /api/checkout`, restrito a `CUSTOMER`
- `GET /api/orders`, restrito a `CUSTOMER`
- `GET /api/orders/{id}`, restrito a `CUSTOMER`
- `POST /api/orders/{id}/cancel`, restrito a `CUSTOMER`
- `GET /api/admin/orders`, restrito a `ADMIN`
- `PUT /api/admin/orders/{id}/status`, restrito a `ADMIN`
- `POST /api/admin/orders/{id}/cancel`, restrito a `ADMIN`

Checkout aceita:

- `couponCode`: codigo opcional de cupom.
- `paymentApproved`: `true` ou omitido simula pagamento aprovado; `false` simula falha de pagamento.

## Testes

Rodar testes do backend:

```bash
cd backend
mvn test
```

Rodar validacoes do frontend:

```bash
cd frontend
npm run lint
npm run build
```

Cobertura critica atual:

- Checkout aprovado reserva estoque, limpa carrinho e preserva snapshot de preco.
- Falha de pagamento nao reserva estoque e nao limpa carrinho.
- Cupom percentual calcula desconto e registra uso unico.
- Cupom expirado e rejeitado.
- Cupom com valor minimo nao atingido e rejeitado.
- Cupom ja usado pelo usuario e rejeitado.
- Duas tentativas simultaneas de checkout para o ultimo item permitem apenas uma compra.

## CI

O workflow `.github/workflows/ci.yml` executa no push/PR para `main`:

- Backend: `mvn test`
- Frontend: `npm ci`, `npm run lint`, `npm run build`

O job backend declara PostgreSQL e Redis como servicos do GitHub Actions.

## Decisoes Tecnicas

- Spring Boot foi escolhido pela robustez em seguranca, transacoes, JPA e testes.
- PostgreSQL foi escolhido como banco relacional principal por integridade, locks e suporte transacional.
- Flyway garante schema versionado e reproducivel do zero.
- Redis atende o requisito de cache externo para produtos.
- Soft delete em produtos preserva integridade historica dos pedidos.
- O checkout usa transacao e lock pessimista nos produtos para evitar overselling.
- Os itens de pedido salvam snapshot de nome, preco unitario e total da linha.
- JWT stateless simplifica a integracao frontend/backend.
- Rate limiting foi implementado em memoria para endpoints publicos exigidos.
- Logs de request sao emitidos em JSON com timestamp, metodo, rota, status e duracao.
- Next.js foi usado no frontend por produtividade e boa experiencia com TypeScript.

## Observabilidade

Health checks:

- `GET /api/health`
- `GET /actuator/health`

Cada request gera um log JSON no backend com o formato:

```json
{"timestamp":"2026-06-08T00:00:00Z","method":"GET","route":"/api/products","statusCode":200,"durationMs":12}
```

## Checklist Do Desafio

- Auth e usuarios: implementado.
- Roles `admin` e `customer`: implementado.
- Protecao de rotas por papel: implementado.
- Catalogo CRUD: implementado.
- Busca, filtros e paginacao: implementado.
- Carrinho persistido: implementado.
- Validacao de estoque no carrinho: implementado.
- Checkout com reserva atomica de estoque: implementado com lock pessimista.
- Precos vigentes no checkout: implementado via snapshot nos itens do pedido.
- Estados de pedido: implementado.
- Cancelamento antes de envio com devolucao de estoque: implementado.
- Simulacao de pagamento: implementado.
- Cupons percentual/fixo, expiracao, uso unico e minimo: implementado.
- Validacao de input: implementado com Bean Validation.
- Erros padronizados: implementado.
- Migrations/schema versioning: implementado com Flyway.
- Seed: implementado.
- Testes automatizados criticos: implementado.
- Rate limiting em login, registro e catalogo: implementado.
- Cache externo de produtos com invalidacao: implementado com Redis.
- CI com build e testes: implementado.
- Logs JSON por request: implementado.
- Swagger/OpenAPI interativo: implementado.
- API + Web funcional: implementado.

## Limitacoes Conhecidas

- Rate limiting esta em memoria; em producao seria preferivel Redis/Bucket4j distribuido.
- Gateway de pagamento real e webhooks nao foram implementados, pois eram opcionais.
- CD/deploy automatizado nao foi implementado, pois era diferencial opcional.
