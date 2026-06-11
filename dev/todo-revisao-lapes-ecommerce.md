# 📋 TODO · Revisão de Conformidade — LAPES E-Commerce
> **Repo avaliado:** `Gaalbu/processo-seletivo-2026`  
> **Blueprint de referência:** `arquitetura-ecommerce-lapes.md`  
> **Gerado em:** 2026-06-11  
> **Uso:** Cada item contém contexto suficiente para um agente de execução inspecionar, corrigir ou verificar sem contexto adicional.

---

## ⚠️ DIVERGÊNCIAS CRÍTICAS (podem reprovar o projeto)

---

### 🔴 C-01 — Java 17 em vez de Java 21 (LTS exigido)

**Arquivo:** `backend/pom.xml`, `README.md`  
**Evidência:** O README declara `Backend: Java 17, Spring Boot 3` enquanto o blueprint exige Java 21 LTS (com virtual threads, records e pattern matching).  
**Impacto:** Requisito técnico explícito da seleção não atendido.

**Ação:**
- [ ] Abrir `backend/pom.xml` e alterar a propriedade `<java.version>` de `17` para `21`.
- [ ] Verificar se o `Dockerfile` do backend usa `FROM eclipse-temurin:17-...` → alterar para `eclipse-temurin:21-jdk-alpine` (ou similar).
- [ ] Atualizar o step `actions/setup-java` no `.github/workflows/ci.yml` para `java-version: '21'`.
- [ ] Rodar `mvn clean verify` após a alteração para garantir compilação sem erros.
- [ ] Conferir se alguma dependência de terceiros é incompatível com Java 21 e resolver.
- [ ] Atualizar o `README.md` para refletir Java 21.

**Verificação final:** `java -version` dentro do container deve retornar `21.x.x`.

---

### 🔴 C-02 — Frontend: Next.js em vez de React 18 + Vite

**Arquivo:** `frontend/package.json`, `README.md`  
**Evidência:** O README declara `Frontend: Next.js, React, TypeScript`. O blueprint especifica `React 18 + Vite` com SPA (Single Page Application). Next.js é um framework SSR/SSG com roteamento próprio — é uma escolha arquitetural diferente.  
**Impacto:** A estrutura de pastas esperada (`frontend/src/pages/`, `vite.config.ts`, `App.tsx`) não existe; o frontend foi construído com convenções Next.js (pasta `app/` ou `pages/`).

**Ação:**
- [ ] Verificar se o avaliador aceita Next.js como substituto. Se não aceitar:
  - [ ] Criar projeto React 18 + Vite: `npm create vite@latest frontend -- --template react-ts`
  - [ ] Recriar todas as páginas e componentes nas rotas descritas no blueprint
  - [ ] Configurar `vite.config.ts` com proxy para `http://localhost:8080`
  - [ ] Substituir o roteamento Next.js por React Router v6
- [ ] Se Next.js for aceito: documentar explicitamente a justificativa técnica no README.
- [ ] Verificar se os hooks de dados (`useProducts`, `useCart`, etc.) usam React Query (TanStack Query) conforme especificado.
- [ ] Confirmar que Zustand está sendo usado para estado global (`authStore`, `cartStore`).

**Verificação final:** `npm run build` no frontend deve gerar bundle sem erros; `npm run dev` deve servir SPA na porta 3000.

---

### 🔴 C-03 — Rate Limiting em memória em vez de Redis/Bucket4j distribuído

**Arquivo:** `README.md` → "Limitações Conhecidas": *"Rate limiting está em memória; em produção seria preferível Redis/Bucket4j distribuído."*  
**Evidência:** O blueprint exige explicitamente `Bucket4j 8.x + Redis` com `ProxyManager`. A implementação atual usa rate limiting in-memory.  
**Impacto:** Em ambiente com múltiplas instâncias, o limite não é compartilhado. O blueprint detalha a integração exata com Redis.

**Ação:**
- [ ] Adicionar dependência no `pom.xml`:
  ```xml
  <dependency>
      <groupId>com.bucket4j</groupId>
      <artifactId>bucket4j-redis</artifactId>
      <version>8.x.x</version>
  </dependency>
  ```
- [ ] Implementar `RateLimitConfig.java` criando um `ProxyManager<String>` com `LettuceBasedProxyManager` (ou `RedissonBasedProxyManager`).
- [ ] Refatorar `RateLimitInterceptor.java` para usar o `ProxyManager` do Redis em vez de `ConcurrentHashMap` local.
- [ ] Garantir que as chaves de rate limit usam o padrão `{path}:{clientIp}` como no blueprint.
- [ ] Testar: reiniciar o container backend e verificar que os tokens consumidos antes do restart são preservados no Redis.

**Verificação final:** Com dois processos backend simultâneos compartilhando o mesmo Redis, o limite global deve ser respeitado (não duplicado).

---

### 🔴 C-04 — `/api/auth/logout` não está documentado no README

**Arquivo:** `README.md` → seção "API"  
**Evidência:** O blueprint exige `POST /api/v1/auth/logout` (invalida token no Redis via `TokenBlacklist`). O README lista apenas `/register`, `/login` e `/me` — logout está ausente.  
**Impacto:** Tokens JWT não são invalidados no servidor; o logout é apenas client-side.

**Ação:**
- [ ] Verificar se `TokenBlacklist.java` (Redis) existe no código fonte.
- [ ] Verificar se `AuthController` tem o endpoint `POST /api/auth/logout`.
- [ ] Se ausente, implementar: ao fazer logout, inserir o JTI (ou token completo) em um Redis Set com TTL igual ao tempo de expiração restante do token.
- [ ] No `JwtAuthFilter`, verificar se o token está na blacklist antes de autenticar.
- [ ] Adicionar o endpoint no README na seção de API.
- [ ] Adicionar teste em `AuthControllerTest` validando que token blacklistado retorna 401.

**Verificação final:** `POST /api/auth/logout` com token válido deve retornar 200; usar o mesmo token em seguida deve retornar 401.

---

### 🔴 C-05 — Prefixo de rotas inconsistente: `/api/v1/` vs `/api/`

**Arquivo:** `README.md`, código fonte  
**Evidência:** O blueprint define todas as rotas com prefixo `/api/v1/` (ex: `/api/v1/products`, `/api/v1/cart`). O README do repo usa `/api/` (ex: `/api/products`, `/api/cart`, `/api/checkout`). Além disso, rotas admin têm prefixo diferente: `/api/admin/products` em vez de `/api/v1/products` (com role ADMIN).  
**Impacto:** Inconsistência de versionamento de API. Sem o `v1`, um deploy futuro sem breaking change retrocompatível é impossível.

**Ação:**
- [ ] Decidir se adotará `/api/v1/` ou `/api/` e padronizar **todos** os controllers.
- [ ] Verificar se o `SecurityConfig.java` usa os prefixos corretos nos `antMatchers`/`requestMatchers`.
- [ ] Atualizar o `nginx.conf` (se existir) para refletir o prefixo correto no proxy reverso.
- [ ] Atualizar as variáveis de ambiente do frontend (`NEXT_PUBLIC_API_URL` ou `VITE_API_URL`).
- [ ] Atualizar o README com as rotas corretas.

**Verificação final:** `curl http://localhost:8080/api/v1/products` deve retornar 200 (ou o prefixo decidido deve ser consistente em 100% dos controllers).

---

## 🟠 DIVERGÊNCIAS MODERADAS (comprometem avaliação técnica)

---

### 🟠 M-01 — Endpoint `PATCH /{id}/stock` ausente para ajuste de estoque

**Arquivo:** Código fonte `ProductController.java`, README  
**Evidência:** O blueprint define `PATCH /api/v1/products/{id}/stock` (ADMIN) para ajuste granular de estoque. O README não lista esse endpoint; apenas `DELETE` para produtos admin existe.  
**Impacto:** Sem esse endpoint, um admin não pode corrigir divergências de estoque sem editar o produto inteiro.

**Ação:**
- [ ] Adicionar em `ProductController.java`:
  ```java
  @PatchMapping("/{id}/stock")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductResponse> updateStock(
      @PathVariable UUID id,
      @RequestBody @Valid StockUpdateRequest request) { ... }
  ```
- [ ] Criar `StockUpdateRequest.java` com campo `int quantity` (pode ser negativo para decremento).
- [ ] Implementar a lógica no `ProductService.java` com validação de estoque não-negativo.
- [ ] Invalidar o cache Redis desse produto ao atualizar o estoque.
- [ ] Documentar no Swagger com `@Operation`.

**Verificação final:** `PATCH /api/v1/products/{id}/stock` com body `{"quantity": 10}` deve retornar 200 com estoque atualizado.

---

### 🟠 M-02 — Soft delete de produtos: verificar se `active=false` filtra listagem pública

**Arquivo:** `ProductRepository.java`, `ProductService.java`  
**Evidência:** O blueprint especifica soft delete (campo `active` na migration V2). O README confirma implementação, mas não há garantia que a query de listagem pública filtra `WHERE active = true`.

**Ação:**
- [ ] Inspecionar o método de listagem em `ProductRepository.java`. Deve conter `AND active = true` ou `findAllByActiveTrue(...)`.
- [ ] Verificar que `ProductService.getById()` lança `EntityNotFoundException` para produto com `active = false`.
- [ ] Verificar que `CartService` rejeita adição de produto inativo.
- [ ] Verificar que o cache Redis não serve produto inativo após soft delete.
- [ ] Adicionar teste unitário: produto deletado (active=false) não deve aparecer na listagem pública.

**Verificação final:** `DELETE /api/v1/products/{id}` seguido de `GET /api/v1/products` não deve retornar o produto deletado.

---

### 🟠 M-03 — `order_status_history` não está documentado no fluxo de pedidos

**Arquivo:** `OrderService.java`, migration V4  
**Evidência:** O blueprint define a tabela `order_status_history` e o método `saveStatusHistory()` em `OrderService`. A migration V4 deve criá-la. Não há menção explícita dessa tabela no README do repo.

**Ação:**
- [ ] Verificar se a migration `V4__create_orders.sql` inclui a criação de `order_status_history`.
- [ ] Verificar se `OrderService.updateStatus()` chama `saveStatusHistory()` em toda transição.
- [ ] Verificar se existe endpoint ou campo na resposta de pedido que expõe o histórico ao cliente/admin.
- [ ] Se ausente, adicionar a tabela na migration e o método no service.

**Verificação final:** Após avançar o status de um pedido, `SELECT * FROM order_status_history WHERE order_id = '...'` deve retornar uma linha com o novo status.

---

### 🟠 M-04 — `idempotency_key` no checkout: verificar se há tratamento real de idempotência

**Arquivo:** `CheckoutService.java`, `OrderRepository.java`  
**Evidência:** O blueprint define o campo `idempotency_key` na tabela `orders` com constraint `UNIQUE`. A migration V4 deve incluir esse campo. O blueprint especifica que é usado para evitar double-submit.

**Ação:**
- [ ] Verificar se a migration V4 tem a coluna `idempotency_key VARCHAR(255) UNIQUE`.
- [ ] Verificar se `CheckoutService` valida: se já existe um pedido com a mesma `idempotency_key` para o usuário, retorna o pedido existente em vez de criar um novo.
- [ ] Verificar se o frontend envia o `idempotency_key` (UUID gerado no cliente antes de submeter).
- [ ] Tratar o `DataIntegrityViolationException` do PostgreSQL (violação de UNIQUE) no `GlobalExceptionHandler` retornando 409 com mensagem clara.

**Verificação final:** Duas requisições simultâneas de checkout com a mesma `idempotency_key` devem resultar em apenas um pedido criado.

---

### 🟠 M-05 — Cancelamento de pedido: verificar se devolve estoque corretamente

**Arquivo:** `OrderService.java` → método `restoreStock()`  
**Evidência:** O blueprint define explicitamente `restoreStock(order)` chamando `productRepository.incrementStock()`. O README afirma que está implementado, mas a integração com o rate limiting/cache deve ser verificada.

**Ação:**
- [ ] Verificar se `productRepository.incrementStock(id, qty)` existe e é atômico (ex: `@Modifying @Query("UPDATE Product p SET p.stock = p.stock + :qty WHERE p.id = :id")`).
- [ ] Verificar se, ao cancelar, o cache Redis do produto é invalidado (`ProductCacheService.evict(productId)`).
- [ ] Verificar a máquina de estados: cancelamento só deve ser permitido nos estados `PENDING` e `PAID` (não `SHIPPED`).
- [ ] Verificar que o endpoint `POST /api/orders/{id}/cancel` (CUSTOMER) restringe cancelamento ao dono do pedido (não qualquer autenticado).
- [ ] Adicionar teste: cancelar pedido → verificar estoque restaurado no banco.

**Verificação final:** Estoque antes do checkout + cancelamento deve ser igual ao estoque depois do cancelamento.

---

### 🟠 M-06 — Validação de cupom: verificar integração com o checkout no Mercado Pago

**Arquivo:** `CheckoutService.java`, `CouponService.java`  
**Evidência:** O repo implementou integração real com Mercado Pago. O blueprint assume que o desconto é calculado localmente e aplicado atomicamente. Com Mercado Pago, o fluxo é assíncrono (pagamento confirmado via webhook) — o cupom não deve ser "consumido" antes da confirmação do pagamento.

**Ação:**
- [ ] Verificar quando `CouponUsage` é registrado no banco: no momento do checkout (antes do pagamento) ou no webhook de confirmação.
- [ ] Se registrado antes da confirmação: se o pagamento falhar, a `CouponUsage` deve ser removida ou o pedido cancelado deve devolver o uso do cupom.
- [ ] Verificar se o cancelamento de pedido também remove a `CouponUsage` correspondente.
- [ ] Verificar que `UNIQUE (coupon_id, user_id)` na tabela `coupon_usages` não bloqueia o usuário de usar o mesmo cupom em um novo pedido se o anterior foi cancelado.

**Verificação final:** Fluxo: aplicar cupom → pedido criado → pagamento falha/cancelado → cupom deve poder ser usado novamente.

---

### 🟠 M-07 — `pg_trgm` extension: a migration V2 cria o índice ANTES da extensão

**Arquivo:** `backend/src/main/resources/db/migration/V2__create_products.sql`  
**Evidência:** No blueprint, a migration V2 cria `idx_products_name_trgm ON products USING GIN (name gin_trgm_ops)` mas a linha `CREATE EXTENSION IF NOT EXISTS pg_trgm` aparece **depois** do índice no documento. Dependendo da ordem de execução, isso pode falhar.

**Ação:**
- [ ] Abrir `V2__create_products.sql` e garantir que `CREATE EXTENSION IF NOT EXISTS pg_trgm` está nas **primeiras linhas** do arquivo, antes de qualquer `CREATE INDEX`.
- [ ] Verificar se o usuário do banco (`lapes` ou `ecommerce`) tem permissão para criar extensões (`SUPERUSER` ou `CREATE` no banco). Se não tiver, mover a extensão para `V1__create_users.sql` com uma role privilegiada, ou criar a extensão manualmente antes de rodar migrations.
- [ ] Testar do zero: `docker compose down -v && docker compose up --build` e observar os logs do Flyway.

**Verificação final:** `SELECT * FROM pg_extension WHERE extname = 'pg_trgm'` deve retornar 1 linha após `docker compose up`.

---

### 🟠 M-08 — `docker-compose.yml` sem healthcheck para Redis

**Arquivo:** `docker-compose.yml`  
**Evidência:** O `docker-compose.yml` do blueprint define healthcheck para PostgreSQL mas não para Redis. O serviço `backend` tem `depends_on: redis: condition: service_started` (não `service_healthy`). Se Redis demorar a inicializar, o backend pode falhar na conexão.

**Ação:**
- [ ] Adicionar healthcheck ao serviço Redis no `docker-compose.yml`:
  ```yaml
  healthcheck:
    test: ["CMD", "redis-cli", "ping"]
    interval: 5s
    timeout: 3s
    retries: 5
  ```
- [ ] Alterar `depends_on` do backend para:
  ```yaml
  redis:
    condition: service_healthy
  ```
- [ ] Testar: `docker compose up --build` e verificar que o backend só sobe após Redis responder a `PING`.

**Verificação final:** `docker compose ps` deve mostrar Redis como `healthy` antes do backend iniciar.

---

### 🟠 M-09 — `docker-compose.yml` usa `version: '3.9'` (campo obsoleto no Compose V2)

**Arquivo:** `docker-compose.yml`  
**Evidência:** O blueprint usa `version: '3.9'`, que foi declarado obsoleto pelo Docker Compose V2. Em versões recentes do Docker Desktop, isso gera aviso. No CI (GitHub Actions com ubuntu-latest) pode gerar falha dependendo da versão instalada.

**Ação:**
- [ ] Remover a linha `version: '3.9'` do topo do `docker-compose.yml` (Compose V2 não requer declaração de versão).
- [ ] Verificar se o arquivo do repo também tem essa linha e remover.
- [ ] Rodar `docker compose config` para validar que o arquivo é válido sem a diretiva `version`.

**Verificação final:** `docker compose config` não deve emitir warnings sobre `version`.

---

## 🟡 MELHORIAS MENORES (qualidade e completude)

---

### 🟡 P-01 — `SeedDataLoader` sem perfil `!prod`: verificar anotação `@Profile`

**Arquivo:** `SeedDataLoader.java` (ou equivalente no repo)  
**Evidência:** O blueprint exige `@Profile("!prod")` para evitar seed em produção. O README menciona que o seed roda automaticamente com Flyway, mas não confirma a anotação de perfil.

**Ação:**
- [ ] Localizar `SeedDataLoader.java` e confirmar que possui `@Profile("!prod")`.
- [ ] Se o seed está embutido em uma migration Flyway (ex: `V6__seed.sql`), garantir que ele só é executado quando `spring.profiles.active != prod`.
- [ ] Verificar que `application-prod.yml` (se existir) tem `spring.flyway.locations` apontando para um path sem o seed.

---

### 🟡 P-02 — CI: job frontend não roda `type-check` (npm run type-check)

**Arquivo:** `.github/workflows/ci.yml`  
**Evidência:** O blueprint define o step `npm run type-check` no job frontend. O README lista apenas `npm run lint` e `npm run build` na cobertura do CI.

**Ação:**
- [ ] Abrir `.github/workflows/ci.yml` e verificar se o step `npm run type-check` existe.
- [ ] Se ausente, adicionar após o build:
  ```yaml
  - name: Type Check
    run: cd frontend && npm run type-check
  ```
- [ ] Garantir que o `package.json` do frontend tem o script: `"type-check": "tsc --noEmit"`.

---

### 🟡 P-03 — CI: job backend sem Redis como service

**Arquivo:** `.github/workflows/ci.yml`  
**Evidência:** O blueprint define apenas PostgreSQL como service no CI. Porém, o backend usa Redis para cache e rate limiting. Testes que dependem de Redis (`ProductCacheService`, `RateLimitInterceptor`) podem falhar no CI se Redis não estiver disponível.

**Ação:**
- [ ] Adicionar Redis como service no job backend do CI:
  ```yaml
  services:
    redis:
      image: redis:7-alpine
      ports:
        - 6379:6379
  ```
- [ ] Verificar se `application-test.yml` (ou variáveis de ambiente do CI) aponta para `localhost:6379`.
- [ ] Se testes de integração com Redis são lentos, criar um profile `ci` com Redis mockado via `@MockBean`.

---

### 🟡 P-04 — `nginx.conf` ausente ou não integrado ao `docker-compose.yml`

**Arquivo:** `nginx/nginx.conf`, `docker-compose.yml`  
**Evidência:** O blueprint define um serviço `nginx` no `docker-compose.yml` e uma pasta `nginx/nginx.conf`. O repo tem a pasta `deploy/` ao invés de `nginx/`, e não é claro se o nginx está configurado como descrito.

**Ação:**
- [ ] Verificar se `nginx/nginx.conf` existe (ou equivalente em `deploy/`).
- [ ] Verificar se o `docker-compose.yml` do repo inclui o serviço nginx com:
  - `rate limiting` configurado (ex: `limit_req_zone`, `limit_req`)
  - proxy reverso para backend na porta 8080
  - proxy para frontend (servindo os assets estáticos ou encaminhando para porta 3000/80)
- [ ] Garantir que o nginx depende de `frontend` e `backend` no Compose.
- [ ] Testar: `docker compose up` e acessar `http://localhost:80` deve servir o frontend.

---

### 🟡 P-05 — `docker-compose.override.yml` ausente

**Arquivo:** `docker-compose.override.yml`  
**Evidência:** O blueprint prevê um `docker-compose.override.yml` para dev overrides (hot reload, volumes de desenvolvimento). O repo não menciona esse arquivo.

**Ação:**
- [ ] Criar `docker-compose.override.yml` com overrides de desenvolvimento:
  ```yaml
  services:
    backend:
      volumes:
        - ./backend:/app
      environment:
        SPRING_PROFILES_ACTIVE: dev
    frontend:
      volumes:
        - ./frontend:/app
        - /app/node_modules
  ```
- [ ] Documentar no README que `docker compose up` usa automaticamente o override em dev.

---

### 🟡 P-06 — `PageResponse<T>` wrapper de paginação: verificar estrutura da resposta

**Arquivo:** `shared/PageResponse.java`, endpoints de listagem  
**Evidência:** O blueprint define `PageResponse.java` como wrapper de paginação global. A resposta de `GET /api/v1/products` deve retornar um objeto com `content`, `totalElements`, `totalPages`, `page`, `size` — e não o `Page<T>` padrão do Spring (que expõe detalhes internos desnecessários).

**Ação:**
- [ ] Verificar se `ProductController` retorna `PageResponse<ProductResponse>` ou `Page<ProductResponse>` do Spring.
- [ ] Se retorna `Page<T>` nativo, criar/verificar `PageResponse.java` e mapear.
- [ ] Verificar consistência: todos os endpoints paginados (`/products`, `/orders/admin`) devem usar o mesmo wrapper.

---

### 🟡 P-07 — Logs JSON: verificar campo `route` vs `path`

**Arquivo:** `RequestLoggingFilter.java`  
**Evidência:** O blueprint define o formato de log com o campo `path`:
```json
{"timestamp":"...","method":"GET","path":"/api/products","status":200,"duration_ms":12}
```
O README do repo usa `route` e `statusCode` em vez de `path` e `status`:
```json
{"timestamp":"...","method":"GET","route":"/api/products","statusCode":200,"durationMs":12}
```

**Ação:**
- [ ] Verificar qual formato está sendo emitido de fato no `RequestLoggingFilter.java`.
- [ ] Padronizar com o nome de campo que o blueprint define (`path`, `status`, `duration_ms`) **ou** documentar claramente no README o formato adotado.
- [ ] Se há um sistema de observabilidade externo esperando um formato específico, usar esse como referência.

---

### 🟡 P-08 — `GlobalExceptionHandler`: verificar se `EntityNotFoundException` retorna 404

**Arquivo:** `GlobalExceptionHandler.java`  
**Evidência:** O blueprint trata `BusinessException` (400) e `InsufficientStockException` (409), mas não lista o handler para `EntityNotFoundException` (produto/pedido não encontrado). Sem esse handler, o Spring retornará 500 genérico.

**Ação:**
- [ ] Adicionar em `GlobalExceptionHandler.java`:
  ```java
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex) {
      return ResponseEntity.status(404)
          .body(new ApiError(404, ex.getMessage(), "NOT_FOUND"));
  }
  ```
- [ ] Verificar que `ProductCacheService.getById()` lança `EntityNotFoundException` (não `RuntimeException`).
- [ ] Testar: `GET /api/v1/products/{uuid-inexistente}` deve retornar 404 com body JSON padronizado.

---

### 🟡 P-09 — `coupon_usages`: restrição UNIQUE bloqueia re-uso após cancelamento

**Arquivo:** `V5__create_coupons.sql`, `CouponService.java`  
**Evidência:** A migration define `UNIQUE (coupon_id, user_id)` em `coupon_usages`. Isso impede que um usuário use o mesmo cupom em dois pedidos diferentes — inclusive se o primeiro pedido foi cancelado. O cupom fica "queimado" mesmo sem conversão.

**Ação:**
- [ ] Revisar a lógica: ao cancelar um pedido que usou cupom, a linha em `coupon_usages` deve ser **removida** (ou marcada como `cancelled`).
- [ ] Adicionar coluna `cancelled_at TIMESTAMPTZ` em `coupon_usages` (nova migration `V6__alter_coupon_usages.sql`) e filtrar pelo status.
- [ ] Alterar a query de validação para contar apenas usos não cancelados: `WHERE cancelled_at IS NULL`.
- [ ] Testar o fluxo completo: usar cupom → cancelar pedido → usar o mesmo cupom em novo pedido → deve funcionar.

---

### 🟡 P-10 — Swagger UI: verificar se todos os endpoints têm anotações `@Operation` e `@Tag`

**Arquivo:** Todos os `*Controller.java`  
**Evidência:** O blueprint lista o SpringDoc OpenAPI como requisito de entrega (`Swagger UI em /swagger-ui.html com todos os endpoints documentados`). Sem `@Tag` e `@Operation`, os endpoints aparecem no Swagger mas sem descrição.

**Ação:**
- [ ] Verificar se `OpenApiConfig.java` define o `@OpenAPIDefinition` com info do projeto.
- [ ] Verificar que cada Controller tem `@Tag(name = "...", description = "...")`.
- [ ] Verificar que os endpoints de segurança sensíveis (`/admin/**`) têm `@SecurityRequirement(name = "Bearer")`.
- [ ] Acessar `http://localhost:8080/swagger-ui.html` e conferir que todos os 25+ endpoints do blueprint estão documentados.

---

## ✅ CHECKLIST FINAL DE CONFORMIDADE

Use esta seção após corrigir os itens acima para validar o projeto completo.

```
STACK
[ ] Java 21 (não 17) — verificar pom.xml e Dockerfile do backend
[ ] Spring Boot 3.3.x — verificar pom.xml
[ ] React 18 + Vite (ou justificativa documentada para Next.js)
[ ] PostgreSQL 16 — verificar docker-compose.yml
[ ] Redis 7.x — verificar docker-compose.yml
[ ] Flyway 10.x — verificar pom.xml

BANCO DE DADOS
[ ] V1 a V5 executam do zero sem erro (docker compose down -v && up)
[ ] pg_trgm extension criada ANTES do índice GIN em V2
[ ] order_status_history criada na migration V4
[ ] idempotency_key UNIQUE na tabela orders (V4)
[ ] coupon_usages com lógica de cancelamento (re-uso após cancel)
[ ] Healthcheck do Redis no docker-compose.yml

AUTENTICAÇÃO
[ ] POST /api/auth/login → retorna JWT
[ ] POST /api/auth/register → cria usuário CUSTOMER
[ ] GET /api/auth/me → retorna perfil do usuário logado
[ ] POST /api/auth/logout → blacklista token no Redis
[ ] Token blacklistado retorna 401

PRODUTOS (admin)
[ ] POST /api/products → cria produto (ADMIN)
[ ] PUT /api/products/{id} → atualiza produto (ADMIN)
[ ] PATCH /api/products/{id}/stock → ajusta estoque (ADMIN)
[ ] DELETE /api/products/{id} → soft delete (ADMIN), campo active=false
[ ] Produto com active=false não aparece na listagem pública

PRODUTOS (público)
[ ] GET /api/products → lista com filtros (name, category, minPrice, maxPrice, page, size, sort)
[ ] GET /api/products/{id} → detalhe do produto
[ ] Busca por nome usa pg_trgm (similaridade de texto)
[ ] Cache Redis com TTL=10min e invalidação em create/update/delete

CARRINHO
[ ] GET /api/cart → carrinho do usuário logado
[ ] POST /api/cart/items → adiciona item, valida estoque, rejeita produto inativo
[ ] PUT /api/cart/items/{itemId} → atualiza quantidade
[ ] DELETE /api/cart/items/{itemId} → remove item
[ ] DELETE /api/cart → limpa carrinho

CHECKOUT
[ ] POST /api/checkout → cria pedido a partir do carrinho
[ ] SELECT FOR UPDATE nos produtos durante checkout
[ ] Preço snapshot dos produtos vigentes (não do carrinho)
[ ] Desconto aplicado se cupom válido
[ ] Estoque decrementado atomicamente
[ ] Carrinho limpo após checkout bem-sucedido
[ ] idempotency_key previne double-submit

PEDIDOS
[ ] GET /api/orders → lista pedidos do usuário autenticado
[ ] GET /api/orders/{id} → detalhe de pedido (apenas do próprio usuário)
[ ] POST /api/orders/{id}/cancel → cancela pedido (PENDING ou PAID)
[ ] Cancelamento devolve estoque (productRepository.incrementStock)
[ ] Cancelamento remove/invalida uso de cupom
[ ] GET /api/admin/orders → todos os pedidos (ADMIN)
[ ] PATCH/PUT /api/admin/orders/{id}/status → avança status (ADMIN)
[ ] Máquina de estados validada: PENDING→PAID→SHIPPED→DELIVERED, CANCELLED terminal

CUPONS
[ ] POST /api/coupons → criar cupom (ADMIN)
[ ] GET /api/coupons → listar cupons (ADMIN)
[ ] PUT /api/coupons/{id} → atualizar cupom (ADMIN)
[ ] DELETE /api/coupons/{id} → desativar cupom (ADMIN)
[ ] POST /api/coupons/validate → validar código com preview do desconto (CUSTOMER)
[ ] Cupom expirado rejeitado
[ ] Cupom abaixo do valor mínimo rejeitado
[ ] Cupom já usado pelo mesmo usuário rejeitado
[ ] Cupom re-usável após cancelamento do pedido anterior

TESTES CRÍTICOS
[ ] CheckoutConcurrencyTest: 2 threads, estoque=1, apenas 1 sucesso
[ ] OrderServiceTest: máquina de estados, transições válidas e inválidas
[ ] CouponServiceTest: expirado, uso único, valor mínimo, percentual, fixo
[ ] AuthControllerTest: registro, login, logout, token inválido

INFRAESTRUTURA
[ ] docker compose up --build sobe tudo sem intervenção manual
[ ] docker compose down -v && up: migrations rodam do zero sem erro
[ ] nginx serve frontend em :80 e faz proxy do backend em :80/api
[ ] Rate limiting Redis/Bucket4j ativo em login, register, catalog
[ ] Logs JSON emitidos a cada request com: timestamp, method, path, status, duration_ms

CI/CD
[ ] .github/workflows/ci.yml existe
[ ] Job backend: mvn clean verify com PostgreSQL E Redis como services
[ ] Job frontend: npm ci + npm run build + npm run type-check
[ ] CI passa no push para main/develop

OBSERVABILIDADE
[ ] GET /actuator/health → retorna UP
[ ] GET /api/health → retorna UP
[ ] Swagger UI acessível em /swagger-ui.html com todos os endpoints documentados

DOCUMENTAÇÃO
[ ] README atualizado com Java 21 (não 17)
[ ] README descreve variáveis de ambiente necessárias
[ ] README tem instruções de setup do zero (docker compose up)
[ ] Decisões técnicas documentadas (especialmente desvios do blueprint)
```

---

## 📌 ORDEM DE PRIORIDADE PARA O AGENTE

Execute os itens na seguinte ordem para maximizar a estabilidade:

1. **C-01** — Migrar Java 17 → 21 (quebra de build se não corrigido)
2. **M-07** — Corrigir ordem da extension pg_trgm (quebra migrations do zero)
3. **M-08** — Adicionar healthcheck Redis no Compose (previne falhas de startup)
4. **C-03** — Implementar Bucket4j + Redis (requisito explícito do blueprint)
5. **C-04** — Implementar logout com TokenBlacklist no Redis
6. **C-05** — Padronizar prefixo de rotas `/api/v1/`
7. **M-01** — Adicionar `PATCH /{id}/stock`
8. **M-02** — Garantir filtro `active=true` nas queries públicas
9. **M-03** — Garantir `order_status_history` na migration e no service
10. **M-04** — Validar idempotência real no checkout
11. **M-05** — Verificar atomicidade do `incrementStock` no cancelamento
12. **M-06** — Alinhar lógica de cupom com fluxo de pagamento Mercado Pago
13. **M-09** — Remover `version: '3.9'` do Compose
14. **P-01 a P-10** — Melhorias de qualidade (executar em paralelo)
15. **Checklist Final** — Validação de conformidade completa
