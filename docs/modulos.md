Poner tamvien modulos.md:
# Módulos a Desarrollar

Sistema de Gestión y Trazabilidad Dietoterápica — Sanatorio "Unidos por la Salud (UxS)"
*(alineado a los Requerimientos Funcionales de la Propuesta de Proyecto, 1.ª Entrega)*

## 1. Autenticación y Perfil Profesional — RF01
Login seguro con JWT, roles (`NUTRICIONISTA`, `COCINA`, `ADMINISTRADOR`) y perfil del nutricionista con nombre, apellido y matrícula.

## 2. Mapeo y Gestión de Camas — RF02
Panel visual de camas organizado por **piso** y **sector**, con estados (Disponible, Ocupada, Mantenimiento, Bloqueada) y asignación/desasignación ágil de pacientes.

## 3. Historia Clínica Nutricional y Evolución — RF03
Registro de datos filiatorios y diagnóstico de base del paciente. Carga de evoluciones **inmutables**, asociadas automáticamente al nutricionista autenticado (firma + matrícula + fecha/hora).

## 4. Preferencias, Alergias y Catálogo de Menús — RF04
Registro de preferencias alimentarias por paciente (Aceptado, Rechazo, Alergia, Intolerancia). Configuración de menús diarios/cíclicos desagregados en **platos** e **ingredientes**, organizados por régimen y turno.

## 5. Motor de Detección de Conflictos (Matching) — RF05
Validación automática entre los ingredientes del menú asignado y las preferencias/alergias del paciente. Notificación visual ante incompatibilidades y sugerencia de plato de reemplazo.

## 6. Módulo de Cocina y Comandas — RF06
Consolidados de producción por turno, piso y régimen. Emisión de rótulos/comandas individuales por cama con advertencias nutricionales visibles.

---

## Requerimientos No Funcionales que debemos tener en cuenta durante el desarrollo

- **RNF01** — Integridad transaccional (ACID): evitar camas duplicadas o dietas solapadas.
- **RNF02** — Contraseñas encriptadas (hash, nunca texto plano).
- **RNF03** — Usabilidad: código de colores estandarizado para regímenes y alertas de alérgenos.
- **RNF04** — Arquitectura en capas: presentación / servicios / persistencia / base de datos, desacopladas.

---
*Cada módulo se corresponde con las tablas del esquema de base de datos (`esquema_bd.sql`).*