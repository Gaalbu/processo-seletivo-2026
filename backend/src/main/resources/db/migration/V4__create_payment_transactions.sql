create table payment_transactions (
  id uuid primary key default gen_random_uuid(),
  order_id uuid not null references orders (id) on delete cascade,
  provider varchar(40) not null,
  provider_payment_id varchar(120) not null,
  status varchar(20) not null,
  amount numeric(12, 2) not null,
  currency varchar(3) not null default 'BRL',
  checkout_url varchar(1000),
  idempotency_key varchar(120) not null unique,
  raw_payload text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint payment_transactions_status_check check (status in ('PENDING', 'APPROVED', 'FAILED')),
  constraint payment_transactions_amount_check check (amount >= 0),
  constraint payment_transactions_provider_payment_unique unique (provider, provider_payment_id)
);

create index idx_payment_transactions_order_id on payment_transactions (order_id);
create index idx_payment_transactions_status on payment_transactions (status);
