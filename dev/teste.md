#### LAPES · PROCESSO SELETIVO 2026

# Sistema E-commerce Simplificado


## Visão Geral

Construir uma aplicação fullstack de um e-commerce simplificado: API backend e interface web.
O desafio avalia capacidade de modelar domínios de negócio, garantir consistência de dados, e
aplicar boas práticas de engenharia de software em um projeto realista.

Não há restrição de tecnologia: linguagem, framework, banco de dados e infraestrutura são de
livre escolha.

Entrega: repositório Git com API + interface web + README documentando setup e decisões
técnicas.

## Domínios de Negócio

### Autenticação & Usuários

- Registro e login com emissão de token
- Dois papéis: admin (gerencia produtos e pedidos) e customer (compra)
- Proteção de rotas por papel

### Catálogo de Produtos

- CRUD completo (criar, listar, detalhar, atualizar, remover)
- Campos: nome, descrição, preço, estoque, categoria, imagem
- Busca com filtros: categoria, faixa de preço, nome.
- Paginação

### Carrinho de Compras

- Carrinho persistido (não efêmero)
- Operações: adicionar item, remover item, atualizar quantidade, limpar carrinho
- Validação de estoque ao adicionar e novamente no checkout
- Um carrinho por usuário autenticado


### Checkout & Pedidos

- Reserva atômica de estoque — duas requisições simultâneas para o último item não podem
    ambas ter sucesso
- Cálculo do total com preços vigentes no momento do checkout
- Máquina de estados do pedido:
**PENDING → PAID → SHIPPED → DELIVERE
D**
- Cancelamento permitido antes de SHIPPED, com devolução de estoque
- Simulação de pagamento com tratamento de sucesso e falha
- _Opcional, mas altamente recomendável: integração com um gateway de pagamento real_
    _(ex: Abacate Pay, Stripe ou outro) com recebimento de status via webhook, substituindo a_
    _simulação fake._

### Cupons de Desconto

- Tipos: percentual ou valor fixo
- Regras de validade: data de expiração e uso único por usuário
- Valor mínimo do pedido para aplicação
- Validação no checkout antes do cálculo final


## Requisitos Obrigatórios

Todos os itens abaixo são obrigatórios. A ausência de qualquer um resulta em penalização.

### Regras de Negócio

**Requisito Detalhe**

**Domínios completos** Todos os 5 domínios (auth, catálogo, carrinho,
checkout, cupons) implementados e funcionais.

**Validação de input** Todas as entradas da API validadas nas bordas:
tipos, formatos, campos obrigatórios.

**Tratamento de erros** Respostas padronizadas com códigos HTTP
corretos. Sem stack traces expostos.

**Schema versioning** Banco com migrations ou equivalente.
Reproduzível do zero.

**Controle de concorrência** Estoque protegido contra overselling sob carga.

**Seed** Script ou comando que popula o banco com
dados de exemplo.

### Qualidade & Engenharia

**Requisito Detalhe**

**Testes automatizados** Cobertura dos fluxos críticos: checkout,
concorrência de estoque, e cupons. Tipo livre
(unit, integração, e2e).

**Rate limiting** Nos endpoints públicos: login, registro, catálogo.

**Cache de produtos** Invalidado ao criar, editar ou remover. Solução
externa obrigatória (Redis, Memcached, etc.).

**Pipeline CI** Pipeline no push/PR com: build e testes.

**Logs** Toda request logada com timestamp, método,
rota, status code, duração. Formato JSON.

**Documentação da API** Swagger/OpenAPI ou equivalente interativo.
Todos os endpoints com exemplos.


### Entrega

**Requisito Detalhe**

**Repositório Git** Código versionado com histórico de commits
limpo e significativo.

**API + Web** Fullstack: backend e interface web funcional
consumindo a API.

**README completo** Instruções de setup, como rodar, stack
escolhida, e decisões técnicas justificadas.
Reproduzível: qualquer avaliador deve conseguir
rodar o projeto.

## Diferenciais (Pontuação Extra)

Não obrigatórios, mas demonstram maturidade técnica e geram pontuação adicional.

- Soft delete em produtos para preservar integridade de pedidos históricos
- Integração com gateway de pagamento real (Abacate Pay, Stripe) com webhooks
- Observabilidade além de logs: métricas, tracing, health check
- Idempotência no checkout (retry seguro)
- CD (deploy automatizado em algum ambiente)

## Regras do Desafio

**Prazo** Entrega até 17 de julho. Apresentações na
primeira semana de agosto (data a decidir).

**Formato** Individual ou dupla.

**Escopo** Fullstack: API backend + interface web.

**Stack** 100% livre — linguagem, framework, banco de
dados, infraestrutura.

**Entrega** Fork Repositório Git com acesso para os
avaliadores.


## Apresentação

Cada participante (ou dupla) fará uma apresentação do projeto aos demais membros do LAPES,
demonstrando a aplicação e justificando as decisões técnicas.

## Critérios de avaliação

```
A nota final é composta por duas partes de peso igual: 50% referente ao desafio técnico e 50%
referente à entrevista com o responsável pela área.
```
### Desafio técnico — 50% da nota final

As categorias abaixo compõem a avaliação do desafio técnico. A avaliação inclui uma rodada de
consultas ao vivo feita pelos avaliadores durante a apresentação, usando a interface do sistema.

### Entrevista — 50% da nota final

Todos os candidatos que entregarem o desafio serão convidados para uma entrevista com o
responsável pela área de interesse. Mais detalhes sobre formato e datas serão divulgados em
breve.

### Entrega — 17 de julho — Individual ou Dupla (Stack Livre)

_O uso de ferramentas de IA Generativa é permitido — ferramentas fazem parte do dia a dia
do desenvolvimento moderno e saber utilizá-las é uma habilidade válida. Porém, o desafio
avalia o seu conhecimento — não o da máquina. Você deve ser capaz de explicar cada linha
do seu código, justificar cada decisão técnica, e defender sua arquitetura na apresentação. Se
usou IA, entenda o que ela gerou e por quê. Copiar sem compreender será evidente na review._

**Bom desafio!** _Dúvidas? Pergunte para GABRIEL MATTOS TEIXEIRA DOS SANTOS,
ISAAC SOUZA ELGRABLY._
