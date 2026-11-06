-- Sistema de Gestión y Trazabilidad Dietoterápica
-- Esquema de Base de Datos (PostgreSQL) — Entrega Final:
-- Modificacion para la entrega final: los valores fijos (roles, estados, turnos, etc.) se guardan como
-- VARCHAR con una restricción CHECK, en vez de CREATE TYPE ... AS ENUM.
-- Los valores son exactamente los mismos; así Hibernate (@Enumerated STRING)
-- puede leer/escribir/validar estas columnas sin configuración extra.


-- Usuarios y autenticación (RF01)
 
CREATE TABLE usuario (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,        -- hash BCrypt, nunca texto plano
    rol         VARCHAR(30) NOT NULL
                CHECK (rol IN ('NUTRICIONISTA', 'COCINA', 'ADMINISTRADOR')),
    activo      BOOLEAN NOT NULL DEFAULT TRUE
);
 
-- Nutricionista "hereda" de Usuario (joined-table inheritance)
CREATE TABLE nutricionista (
    usuario_id  BIGINT PRIMARY KEY REFERENCES usuario(id) ON DELETE CASCADE,
    nombre      VARCHAR(100) NOT NULL,
    apellido    VARCHAR(100) NOT NULL,
    matricula   VARCHAR(30) NOT NULL UNIQUE  -- provincial o nacional, por eso VARCHAR
);
 
-- Pacientes, historia clínica y evoluciones (RF03)
 
CREATE TABLE historia_clinica (
    id      BIGSERIAL PRIMARY KEY
);
 
CREATE TABLE paciente (
    id                  BIGSERIAL PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL,
    apellido            VARCHAR(100) NOT NULL,
    diagnostico_base    TEXT,                          -- RF03: diagnóstico médico de base
    historia_clinica_id BIGINT NOT NULL UNIQUE REFERENCES historia_clinica(id),
    fecha_ingreso       DATE NOT NULL,
    fecha_alta          DATE                            -- NULL mientras sigue internado
);
 
-- Registro inmutable: solo se insertan filas, nunca se actualizan/borran (a nivel aplicación)
CREATE TABLE evolucion (
    id                  BIGSERIAL PRIMARY KEY,
    historia_clinica_id BIGINT NOT NULL REFERENCES historia_clinica(id) ON DELETE CASCADE,
    fecha_hora          TIMESTAMP NOT NULL DEFAULT now(),
    nota                TEXT NOT NULL,
    nutricionista_id    BIGINT NOT NULL REFERENCES nutricionista(usuario_id)  -- "firma" digital
);
 
-- Camas (RF02) — mapeo por piso y sector
 
CREATE TABLE cama (
    id          BIGSERIAL PRIMARY KEY,
    numero      VARCHAR(20) NOT NULL,
    piso        INTEGER NOT NULL,
    sector      VARCHAR(30) NOT NULL
                CHECK (sector IN ('PEDIATRIA', 'MATERNIDAD', 'CIRUGIA', 'CLINICA',
                                  'TERAPIA', 'TRAUMATOLOGIA', 'UROLOGIA')),
    estado      VARCHAR(30) NOT NULL DEFAULT 'DISPONIBLE'
                CHECK (estado IN ('DISPONIBLE', 'OCUPADA', 'MANTENIMIENTO', 'BLOQUEADA')),
    paciente_id BIGINT UNIQUE REFERENCES paciente(id)   -- NULL si no está ocupada
);
 
-- Alimentos y preferencias (RF04)
 
CREATE TABLE alimento (
    id      BIGSERIAL PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL UNIQUE
);
 
CREATE TABLE preferencia_alimentaria (
    id          BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL REFERENCES paciente(id) ON DELETE CASCADE,
    alimento_id BIGINT NOT NULL REFERENCES alimento(id),
    tipo        VARCHAR(30) NOT NULL
                CHECK (tipo IN ('ACEPTADO', 'RECHAZO', 'ALERGIA', 'INTOLERANCIA')),
    UNIQUE (paciente_id, alimento_id)  -- un paciente no puede tener 2 preferencias sobre el mismo alimento
);
 
-- Regímenes, menús y platos (RF04) — "menús desagregados por platos e ingredientes"

 
CREATE TABLE regimen (
    id      BIGSERIAL PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL,
    tipo    VARCHAR(30) NOT NULL
            CHECK (tipo IN ('GENERAL', 'LIQUIDO', 'BLANDO', 'BLANDO_CON_POLLO',
                            'HIPOSODICO', 'BLANDO_MASTICATORIO', 'DIABETICO',
                            'CELIACO', 'INDIVIDUAL'))
);
 
CREATE TABLE menu (
    id          BIGSERIAL PRIMARY KEY,
    regimen_id  BIGINT NOT NULL REFERENCES regimen(id) ON DELETE CASCADE,
    turno       VARCHAR(30) NOT NULL
                CHECK (turno IN ('DESAYUNO', 'ALMUERZO', 'MERIENDA', 'CENA')),
    fecha       DATE NOT NULL   -- permite menús diarios o cíclicos según cómo se pueble
);
 
CREATE TABLE plato (
    id      BIGSERIAL PRIMARY KEY,
    menu_id BIGINT NOT NULL REFERENCES menu(id) ON DELETE CASCADE,
    nombre  VARCHAR(100) NOT NULL
);
 
-- Ingredientes de cada plato (N:M)
CREATE TABLE plato_alimento (
    plato_id    BIGINT NOT NULL REFERENCES plato(id) ON DELETE CASCADE,
    alimento_id BIGINT NOT NULL REFERENCES alimento(id) ON DELETE CASCADE,
    PRIMARY KEY (plato_id, alimento_id)
);
 
-- Índices sugeridos
 
CREATE INDEX idx_evolucion_historia ON evolucion(historia_clinica_id);
CREATE INDEX idx_preferencia_paciente ON preferencia_alimentaria(paciente_id);
CREATE INDEX idx_menu_regimen ON menu(regimen_id);
CREATE INDEX idx_plato_menu ON plato(menu_id);
CREATE INDEX idx_cama_piso_sector ON cama(piso, sector);