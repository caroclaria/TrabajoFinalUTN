-- Datos de ejemplo para el módulo de Regímenes, Menús y Matching (RF04 + RF05).
-- Correr DESPUÉS de esquema_bd.sql (o de migracion_regimen_menus.sql).
--
-- Escenario para la demo del matching:
--   Lista 1 - GENERAL - ALMUERZO = Milanesa con puré + Ensalada mixta + Flan casero
--   Si a un paciente con régimen GENERAL se le carga ALERGIA a "Huevo" y RECHAZO a "Tomate",
--   el matching marca la milanesa y el flan (huevo, CRITICA) y la ensalada (tomate, BAJA),
--   y sugiere como reemplazo platos de otros almuerzos GENERAL sin esos alimentos
--   (Pollo al horno con calabaza, Ensalada de zanahoria y lechuga, Manzana asada).

INSERT INTO alimento (nombre) VALUES
  ('Carne vacuna'), ('Pollo'), ('Huevo'), ('Pan rallado'), ('Papa'), ('Leche'),
  ('Manteca'), ('Lechuga'), ('Tomate'), ('Zanahoria'), ('Calabaza'), ('Manzana'),
  ('Azúcar'), ('Arroz'), ('Harina de trigo'), ('Queso'), ('Pescado'), ('Aceite de girasol');

INSERT INTO plato (nombre) VALUES
  ('Milanesa de carne con puré'),       -- 1
  ('Ensalada mixta'),                   -- 2
  ('Flan casero'),                      -- 3
  ('Pollo al horno con calabaza'),      -- 4
  ('Ensalada de zanahoria y lechuga'),  -- 5
  ('Manzana asada'),                    -- 6
  ('Arroz con queso'),                  -- 7
  ('Pescado al vapor con arroz');       -- 8

INSERT INTO plato_ingrediente (plato_id, alimento_id, cantidad, unidad_medida)
SELECT p.id, a.id, v.cantidad, v.unidad
FROM (VALUES
  ('Milanesa de carne con puré',      'Carne vacuna',      150, 'g'),
  ('Milanesa de carne con puré',      'Huevo',               1, 'unidad'),
  ('Milanesa de carne con puré',      'Pan rallado',        40, 'g'),
  ('Milanesa de carne con puré',      'Papa',              200, 'g'),
  ('Milanesa de carne con puré',      'Leche',              50, 'ml'),
  ('Ensalada mixta',                  'Lechuga',            60, 'g'),
  ('Ensalada mixta',                  'Tomate',             80, 'g'),
  ('Ensalada mixta',                  'Aceite de girasol',  10, 'ml'),
  ('Flan casero',                     'Huevo',               2, 'unidad'),
  ('Flan casero',                     'Leche',             150, 'ml'),
  ('Flan casero',                     'Azúcar',             30, 'g'),
  ('Pollo al horno con calabaza',     'Pollo',             180, 'g'),
  ('Pollo al horno con calabaza',     'Calabaza',          150, 'g'),
  ('Pollo al horno con calabaza',     'Aceite de girasol',  10, 'ml'),
  ('Ensalada de zanahoria y lechuga', 'Zanahoria',          80, 'g'),
  ('Ensalada de zanahoria y lechuga', 'Lechuga',            50, 'g'),
  ('Manzana asada',                   'Manzana',             1, 'unidad'),
  ('Manzana asada',                   'Azúcar',             10, 'g'),
  ('Arroz con queso',                 'Arroz',              80, 'g'),
  ('Arroz con queso',                 'Queso',              30, 'g'),
  ('Arroz con queso',                 'Manteca',            10, 'g'),
  ('Pescado al vapor con arroz',      'Pescado',           160, 'g'),
  ('Pescado al vapor con arroz',      'Arroz',              70, 'g')
) AS v(plato, alimento, cantidad, unidad)
JOIN plato p ON p.nombre = v.plato
JOIN alimento a ON a.nombre = v.alimento;

INSERT INTO menu (numero_lista, tipo_regimen, turno) VALUES
  (1, 'GENERAL', 'ALMUERZO'),
  (2, 'GENERAL', 'ALMUERZO'),
  (1, 'GENERAL', 'CENA'),
  (1, 'BLANDO',  'ALMUERZO');

INSERT INTO menu_plato (menu_id, plato_id)
SELECT m.id, p.id
FROM (VALUES
  (1, 'GENERAL', 'ALMUERZO', 'Milanesa de carne con puré'),
  (1, 'GENERAL', 'ALMUERZO', 'Ensalada mixta'),
  (1, 'GENERAL', 'ALMUERZO', 'Flan casero'),
  (2, 'GENERAL', 'ALMUERZO', 'Pollo al horno con calabaza'),
  (2, 'GENERAL', 'ALMUERZO', 'Ensalada de zanahoria y lechuga'),
  (2, 'GENERAL', 'ALMUERZO', 'Manzana asada'),
  (1, 'GENERAL', 'CENA',     'Arroz con queso'),
  (1, 'GENERAL', 'CENA',     'Manzana asada'),
  (1, 'BLANDO',  'ALMUERZO', 'Pescado al vapor con arroz'),
  (1, 'BLANDO',  'ALMUERZO', 'Manzana asada')
) AS v(lista, regimen, turno, plato)
JOIN menu m ON m.numero_lista = v.lista AND m.tipo_regimen = v.regimen AND m.turno = v.turno
JOIN plato p ON p.nombre = v.plato;
