-- Migración del módulo de Regímenes, Menús, Platos y Preferencias (RF04 + RF05)
--
-- Para bases YA creadas con la versión anterior de esquema_bd.sql (ej: la de Neon).
-- Una base nueva no lo necesita: alcanza con correr esquema_bd.sql completo.
--
-- Qué cambia:
--   1. Se elimina la tabla "regimen": el régimen pasa a ser un valor fijo (enum TipoRegimen).
--   2. Los menús pasan a ser cíclicos: lista (1 a 10) + régimen + turno, en vez de fecha.
--   3. Un plato puede estar en varios menús (tabla menu_plato) y sus ingredientes tienen cantidad.
--   4. El paciente guarda su régimen indicado (paciente.tipo_regimen).
--
-- ATENCIÓN: borra los datos que hubiera en menu, plato, plato_alimento y regimen.

BEGIN;

DROP TABLE IF EXISTS plato_alimento CASCADE;
DROP TABLE IF EXISTS plato CASCADE;
DROP TABLE IF EXISTS menu CASCADE;
DROP TABLE IF EXISTS regimen CASCADE;

CREATE TABLE plato (
    id      BIGSERIAL PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE plato_ingrediente (
    plato_id      BIGINT NOT NULL REFERENCES plato(id) ON DELETE CASCADE,
    alimento_id   BIGINT NOT NULL REFERENCES alimento(id),
    cantidad      DOUBLE PRECISION NOT NULL CHECK (cantidad > 0),
    unidad_medida VARCHAR(30),
    PRIMARY KEY (plato_id, alimento_id)
);

CREATE TABLE menu (
    id            BIGSERIAL PRIMARY KEY,
    numero_lista  INTEGER NOT NULL CHECK (numero_lista BETWEEN 1 AND 10),
    tipo_regimen  VARCHAR(30) NOT NULL
                  CHECK (tipo_regimen IN ('GENERAL', 'LIQUIDO', 'BLANDO', 'BLANDO_CON_POLLO',
                                          'HIPOSODICO', 'BLANDO_MASTICATORIO', 'DIABETICO',
                                          'CELIACO', 'INDIVIDUAL')),
    turno         VARCHAR(30) NOT NULL
                  CHECK (turno IN ('DESAYUNO', 'ALMUERZO', 'MERIENDA', 'CENA')),
    UNIQUE (numero_lista, tipo_regimen, turno)
);

CREATE TABLE menu_plato (
    menu_id   BIGINT NOT NULL REFERENCES menu(id) ON DELETE CASCADE,
    plato_id  BIGINT NOT NULL REFERENCES plato(id),
    PRIMARY KEY (menu_id, plato_id)
);

ALTER TABLE paciente ADD COLUMN IF NOT EXISTS tipo_regimen VARCHAR(30)
    CHECK (tipo_regimen IN ('GENERAL', 'LIQUIDO', 'BLANDO', 'BLANDO_CON_POLLO',
                            'HIPOSODICO', 'BLANDO_MASTICATORIO', 'DIABETICO',
                            'CELIACO', 'INDIVIDUAL'));

CREATE INDEX IF NOT EXISTS idx_menu_regimen_turno ON menu(tipo_regimen, turno);
CREATE INDEX IF NOT EXISTS idx_plato_ingrediente_alimento ON plato_ingrediente(alimento_id);

COMMIT;
