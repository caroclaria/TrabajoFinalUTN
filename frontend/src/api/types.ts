// Tipos que reflejan los DTOs del backend (lo que devuelve y espera la API)

export const ROLES = ['ADMINISTRADOR', 'NUTRICIONISTA', 'COCINA'] as const
export type Rol = (typeof ROLES)[number]

export const SECTORES = [
  'PEDIATRIA',
  'MATERNIDAD',
  'CIRUGIA',
  'CLINICA',
  'TERAPIA',
  'TRAUMATOLOGIA',
  'UROLOGIA',
] as const
export type Sector = (typeof SECTORES)[number]

export const ESTADOS_CAMA = ['DISPONIBLE', 'OCUPADA', 'MANTENIMIENTO', 'BLOQUEADA'] as const
export type EstadoCama = (typeof ESTADOS_CAMA)[number]

export interface LoginResponse {
  token: string
  username: string
  rol: Rol
}

// Perfil del usuario logueado (GET /api/auth/me). Los datos profesionales solo existen si es nutricionista.
export interface Perfil {
  id: number
  username: string
  rol: Rol
  nombre: string | null
  apellido: string | null
  matricula: string | null
}

export interface Usuario {
  id: number
  username: string
  rol: Rol
  activo: boolean
}

export interface Nutricionista {
  id: number
  nombre: string
  apellido: string
  matricula: string
}

export interface Cama {
  id: number
  numero: string
  piso: number
  sector: Sector
  estado: EstadoCama
  pacienteId: number | null
  pacienteNombre: string | null
}

export interface Paciente {
  id: number
  nombre: string
  apellido: string
  diagnosticoBase: string | null
  historiaClinicaId: number
  fechaIngreso: string
  fechaAlta: string | null
  camaId: number | null
  camaNumero: string | null
}

export interface Evolucion {
  id: number
  fechaHora: string
  nota: string
  nutricionistaNombre: string
  nutricionistaApellido: string
  nutricionistaMatricula: string
}
