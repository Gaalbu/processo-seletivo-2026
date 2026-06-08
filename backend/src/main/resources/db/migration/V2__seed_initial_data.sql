insert into users (id, name, email, password_hash, role)
values
  ('00000000-0000-0000-0000-000000000001', 'Admin LAPES', 'admin@lapes.test', '$2a$10$kHEQY5cOXXEvdNQe3ZA6te5eAnNrkX47S1NCQxUKyYPlXJLuodwRW', 'ADMIN'),
  ('00000000-0000-0000-0000-000000000002', 'Cliente LAPES', 'cliente@lapes.test', '$2a$10$kHEQY5cOXXEvdNQe3ZA6te5eAnNrkX47S1NCQxUKyYPlXJLuodwRW', 'CUSTOMER');

insert into carts (id, user_id)
values
  ('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002');

insert into products (id, name, description, price, stock, category, image_url)
values
  ('20000000-0000-0000-0000-000000000001', 'Teclado Mecânico Terminal Pro', 'Teclado mecânico compacto com switches táteis e iluminação verde terminal.', 429.90, 12, 'perifericos', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3'),
  ('20000000-0000-0000-0000-000000000002', 'Mouse Precision CLI', 'Mouse ergonômico de alta precisão para setups de desenvolvimento.', 189.90, 25, 'perifericos', 'https://images.unsplash.com/photo-1527814050087-3793815479db'),
  ('20000000-0000-0000-0000-000000000003', 'Monitor Dark Mode 27', 'Monitor QHD de 27 polegadas com alto contraste para longas sessões de trabalho.', 1699.00, 6, 'monitores', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf'),
  ('20000000-0000-0000-0000-000000000004', 'Headset Deploy Wireless', 'Headset sem fio com microfone removível e bateria de longa duração.', 549.90, 9, 'audio', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e'),
  ('20000000-0000-0000-0000-000000000005', 'Dock Station Stack', 'Dock USB-C com múltiplas portas para notebooks e workstations.', 799.90, 4, 'acessorios', 'https://images.unsplash.com/photo-1625842268584-8f3296236761');

insert into coupons (id, code, type, value, minimum_order_amount, expires_at, active)
values
  ('30000000-0000-0000-0000-000000000001', 'TERMINAL10', 'PERCENTAGE', 10.00, 100.00, '2026-12-31 23:59:59+00', true),
  ('30000000-0000-0000-0000-000000000002', 'SHIP50', 'FIXED_AMOUNT', 50.00, 300.00, '2026-12-31 23:59:59+00', true),
  ('30000000-0000-0000-0000-000000000003', 'EXPIRED20', 'PERCENTAGE', 20.00, 100.00, '2025-01-01 00:00:00+00', true);
