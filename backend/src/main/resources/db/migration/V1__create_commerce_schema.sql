create extension if not exists pgcrypto;

create table users (
  id uuid primary key default gen_random_uuid(),
  name varchar(120) not null,
  email varchar(160) not null unique,
  password_hash varchar(255) not null,
  role varchar(20) not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint users_role_check check (role in ('ADMIN', 'CUSTOMER'))
);

create table products (
  id uuid primary key default gen_random_uuid(),
  name varchar(160) not null,
  description text not null,
  price numeric(12, 2) not null,
  stock integer not null,
  category varchar(80) not null,
  image_url varchar(500) not null,
  deleted_at timestamptz,
  version bigint not null default 0,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint products_price_check check (price >= 0),
  constraint products_stock_check check (stock >= 0)
);

create index idx_products_category on products (category);
create index idx_products_price on products (price);
create index idx_products_name on products using gin (to_tsvector('portuguese', name));

create table carts (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null unique references users (id) on delete cascade,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table cart_items (
  id uuid primary key default gen_random_uuid(),
  cart_id uuid not null references carts (id) on delete cascade,
  product_id uuid not null references products (id),
  quantity integer not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint cart_items_quantity_check check (quantity > 0),
  constraint cart_items_cart_product_unique unique (cart_id, product_id)
);

create table coupons (
  id uuid primary key default gen_random_uuid(),
  code varchar(40) not null unique,
  type varchar(20) not null,
  value numeric(12, 2) not null,
  minimum_order_amount numeric(12, 2) not null default 0,
  expires_at timestamptz not null,
  active boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint coupons_type_check check (type in ('PERCENTAGE', 'FIXED_AMOUNT')),
  constraint coupons_value_check check (value > 0),
  constraint coupons_minimum_check check (minimum_order_amount >= 0)
);

create table orders (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references users (id),
  coupon_id uuid references coupons (id),
  subtotal_amount numeric(12, 2) not null,
  discount_amount numeric(12, 2) not null default 0,
  total_amount numeric(12, 2) not null,
  status varchar(20) not null,
  payment_status varchar(20) not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint orders_status_check check (status in ('PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
  constraint orders_payment_status_check check (payment_status in ('PENDING', 'APPROVED', 'FAILED')),
  constraint orders_subtotal_check check (subtotal_amount >= 0),
  constraint orders_discount_check check (discount_amount >= 0),
  constraint orders_total_check check (total_amount >= 0)
);

create index idx_orders_user_id on orders (user_id);
create index idx_orders_status on orders (status);

create table order_items (
  id uuid primary key default gen_random_uuid(),
  order_id uuid not null references orders (id) on delete cascade,
  product_id uuid not null references products (id),
  product_name varchar(160) not null,
  unit_price numeric(12, 2) not null,
  quantity integer not null,
  line_total numeric(12, 2) not null,
  constraint order_items_unit_price_check check (unit_price >= 0),
  constraint order_items_quantity_check check (quantity > 0),
  constraint order_items_line_total_check check (line_total >= 0)
);

create table coupon_usages (
  id uuid primary key default gen_random_uuid(),
  coupon_id uuid not null references coupons (id),
  user_id uuid not null references users (id),
  order_id uuid not null unique references orders (id),
  used_at timestamptz not null default now(),
  constraint coupon_usages_coupon_user_unique unique (coupon_id, user_id)
);
