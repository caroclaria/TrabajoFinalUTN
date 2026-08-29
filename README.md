# Sistema de Gestión y Trazabilidad Dietoterápica

Trabajo Final Integrador — Tecnicatura Universitaria en Programación (UTN)

## Descripción

Plataforma web para la digitalización del servicio de alimentación y dietoterapia del Sanatorio "Unidos por la Salud (UxS)", que permite gestionar camas, historias clínicas nutricionales, regímenes dietoterápicos y detección automática de incompatibilidades alimentarias (alergias/rechazos) en los menús.

## Integrantes

- Claria, Carolina
- Diaz, Natalia
- Herrera, Jazmin

**Tutor:** Juan Ignacio Schiavonni

## Tecnologías utilizadas

| Capa | Tecnología |
|---|---|
| Frontend | TypeScript + React (Vite) + Tailwind CSS |
| Backend | Java + Spring Boot |
| Seguridad | Spring Security + JWT |
| Persistencia | Spring Data JPA (Hibernate) |
| Base de datos | PostgreSQL |
| Despliegue | Vercel (frontend) + Render (backend y base de datos) |

## Estructura del repositorio

```
/frontend   → Aplicación React (cliente web)
/backend    → API REST en Spring Boot
/docs       → Documentación, propuestas e informes de avance
```

## Instalación y ejecución local

### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## Documentación

Ver `/docs` para la propuesta de proyecto, esquema de base de datos e informes de avance.

## Estado del proyecto

🔵 1.ª Entrega — Propuesta y estructura del Repositorio.
