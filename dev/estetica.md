### ✦ OPÇÃO ESCOLHIDA — "Terminal Moderno" (Blueprint de Interface)

**Conceito:** Estética de editor de código e CLI (Command Line Interface) com apelo comercial. Uma interface *Dark Mode First*, focada em desenvolvedores e entusiastas de tecnologia, que transmite a sensação de um ambiente de desenvolvimento limpo, rápido e responsivo.

---

#### 1. TOKENS DE DESIGN (DESIGN TOKENS)

**1.1. Paleta de Cores (Dark Theme)**

* **Background Principal (App):** `#0D1117` (Preto profundo, minimiza fadiga visual)
* **Background Superfície (Cards, Modals, Drawers):** `#161B22`
* **Background Superfície Hover:** `#21262D`
* **Bordas e Divisores:** `#30363D`
* **Texto Principal (Corpo):** `#C9D1D9` (Cinza claro, alto contraste confortável)
* **Texto Secundário (Muted/Hints):** `#8B949E`
* **Accent Principal (Primária/Sucesso/Estoque):** `#39D353` (Verde Terminal)
* **Accent Secundário (Links/Ações Informativas):** `#58A6FF` (Azul Tech)
* **Aviso (Warning/Pendente):** `#D2A8FF` (Roxo) ou `#E3B341` (Amarelo)
* **Erro (Destrutivo/Sem Estoque):** `#F85149` (Vermelho)

**1.2. Tipografia**

* **Fonte Display / Dados Técnicos (Preços, SKUs, Status, Botões):** `JetBrains Mono` ou `Fira Code`.
* *Uso:* Traz a sensação de código e precisão.


* **Fonte Base (Corpo de texto, Descrições longas):** `Inter` ou `Roboto`.
* *Uso:* Garante legibilidade em textos corridos.


* **Pesos (Font Weights):** `400` (Regular) para corpo, `600` (Semi-bold) para destaques e `700` (Bold) para títulos principais.

**1.3. Espaçamento e Bordas**

* **Border Radius:** `6px` (Arredondamento sutil, característico de editores de código modernos, sem ser excessivamente redondo).
* **Espaçamento Base (Grid de 8px):** `xs: 4px`, `sm: 8px`, `md: 16px`, `lg: 24px`, `xl: 32px`.

---

#### 2. COMPONENTES VISUAIS

**2.1. Botões (CTAs)**

* **Primário (Adicionar ao Carrinho / Finalizar Compra):**
* Fundo: `#238636`
* Texto: `#FFFFFF` (Fonte: Inter, weight 600)
* Borda: `1px solid rgba(240, 246, 252, 0.1)`
* Hover: Fundo muda para `#2EA043`, leve aumento de brilho.


* **Secundário (Terminal Ghost Button):**
* Fundo: Transparente
* Texto: `#39D353` (Fonte: JetBrains Mono)
* Borda: `1px solid #39D353`
* Hover: Fundo `rgba(57, 211, 83, 0.1)` (micro-glow verde) com cursor `[ >_ ]`.



**2.2. Product Cards (Catálogo)**

* **Container:** Fundo `#161B22`, borda `1px solid #30363D`, radius `6px`.
* **Imagem:** Filtro sutil de contraste para adequação ao dark mode. Aspect-ratio quadrado (1:1).
* **Preço:** Fonte `JetBrains Mono`, cor `#58A6FF` (Azul Tech), tamanho `1.25rem`.
* **Badge de Estoque:** No canto superior direito da imagem. Estilo miniatura: Fundo `#39D353` com 15% de opacidade, texto verde `#39D353`, borda `1px solid rgba(57, 211, 83, 0.3)`. Ex: `[ STOCK: OK ]`.
* **Interação (Hover):** A borda do card transita para `#8B949E` e ganha um *micro-glow* `box-shadow: 0 0 8px rgba(57, 211, 83, 0.15)`.

**2.3. Inputs e Formulários (Checkout / Login)**

* **Campo:** Fundo `#0D1117`, borda `1px solid #30363D`, cor do texto `#C9D1D9`.
* **Foco (Active):** A borda muda para `#58A6FF` com um ring outline de `1px` da mesma cor.
* **Labels:** Fonte monoespaçada, em caixa alta e tamanho `0.75rem`. Ex: `// E-MAIL ADDRESS`.

**2.4. Feedbacks e Notificações (Toasts)**

* **Estilo Console:** Os *toasts* de notificação (ex: "Item adicionado ao carrinho") devem aparecer no canto inferior direito.
* **Design:** Fundo `#161B22`, borda esquerda grossa (`4px`) na cor do status (Verde para sucesso, Vermelho para erro).
* **Texto:** Prefixado com o símbolo de terminal. Exemplo: `>_ product_added: "Teclado Mecânico RGB"`.
* **Animação:** Efeito de *typewriter* (digitação rápida) de 200ms na entrada do toast.

**2.5. Status do Pedido (Pipeline Breadcrumbs)**

* Em vez de barras de progresso tradicionais, o status da entrega segue um modelo visual de pipeline de CI/CD (ex: GitHub Actions).
* **Etapas:** `[PENDING] -- [PAID] -- [SHIPPED] -- [DELIVERED]`
* **Estados:**
* Concluído: Ícone de *check* verde `(✓)`, texto `#C9D1D9`.
* Atual: Spinner estilo CLI `[ - \ | / ]` animado ou ponto pulsante verde, texto `#39D353` bold.
* Pendente: Ícone de círculo vazio, texto mutado `#8B949E`.



**2.6. Layout Base**

* **Header (Navbar):** Fixo, fundo translúcido `#0D1117` com *backdrop-blur*, contendo o logo em tipografia monoespaçada (ex: `<LAPES.commerce/>`), barra de busca central estilo "Command Palette" (atalho `Ctrl+K`) e atalho do carrinho à direita indicando quantidade de itens em colchetes `CART [3]`.
* **Carrinho (Drawer Lateral):** Desliza da direita. Fundo `#161B22`. Cada item listado como uma linha de execução, com botões incrementais minimalistas `[-] 1 [+]`.