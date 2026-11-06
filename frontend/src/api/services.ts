// Un objeto por recurso del backend, con una función por endpoint.
import { request } from './client'
import type {
  Cama,
  Evolucion,
  EstadoCama,
  LoginResponse,
  Nutricionista,
  Paciente,
  Perfil,
  Rol,
  Sector,
  Usuario,
} from './types'

export const authApi = {
  login: (username: string, password: string) =>
    request<LoginResponse>('/api/auth/login', { method: 'POST', body: { username, password }, auth: false }),
  me: () => request<Perfil>('/api/auth/me'),
}

export interface CamaForm {
  numero: string
  piso: number
  sector: Sector
}

export const camasApi = {
  listar: () => request<Cama[]>('/api/camas'),
  crear: (data: CamaForm) => request<Cama>('/api/camas', { method: 'POST', body: data }),
  editar: (id: number, data: CamaForm) => request<Cama>(`/api/camas/${id}`, { method: 'PUT', body: data }),
  cambiarEstado: (id: number, estado: EstadoCama) =>
    request<Cama>(`/api/camas/${id}/estado?estado=${estado}`, { method: 'PATCH' }),
  asignar: (id: number, pacienteId: number) =>
    request<Cama>(`/api/camas/${id}/asignar`, { method: 'POST', body: { pacienteId } }),
  desasignar: (id: number) => request<Cama>(`/api/camas/${id}/desasignar`, { method: 'POST' }),
  eliminar: (id: number) => request<void>(`/api/camas/${id}`, { method: 'DELETE' }),
}

export interface PacienteForm {
  nombre: string
  apellido: string
  diagnosticoBase: string
  fechaIngreso?: string
}

export const pacientesApi = {
  listar: (soloInternados: boolean) =>
    request<Paciente[]>(`/api/pacientes?soloInternados=${soloInternados}`),
  obtener: (id: number) => request<Paciente>(`/api/pacientes/${id}`),
  crear: (data: PacienteForm) => request<Paciente>('/api/pacientes', { method: 'POST', body: data }),
  editar: (id: number, data: PacienteForm) =>
    request<Paciente>(`/api/pacientes/${id}`, { method: 'PUT', body: data }),
  darAlta: (id: number) => request<Paciente>(`/api/pacientes/${id}/alta`, { method: 'PATCH' }),
  evoluciones: (id: number) => request<Evolucion[]>(`/api/pacientes/${id}/evoluciones`),
  crearEvolucion: (id: number, nota: string) =>
    request<Evolucion>(`/api/pacientes/${id}/evoluciones`, { method: 'POST', body: { nota } }),
}

export const usuariosApi = {
  listar: () => request<Usuario[]>('/api/usuarios'),
  crear: (data: { username: string; password: string; rol: Rol }) =>
    request<Usuario>('/api/usuarios', { method: 'POST', body: data }),
  editar: (id: number, data: { rol?: Rol; activo?: boolean }) =>
    request<Usuario>(`/api/usuarios/${id}`, { method: 'PUT', body: data }),
  eliminar: (id: number) => request<void>(`/api/usuarios/${id}`, { method: 'DELETE' }),
}

export interface NutricionistaCreateForm {
  username: string
  password: string
  nombre: string
  apellido: string
  matricula: string
}

export const nutricionistasApi = {
  listar: () => request<Nutricionista[]>('/api/nutricionistas'),
  crear: (data: NutricionistaCreateForm) =>
    request<Nutricionista>('/api/nutricionistas', { method: 'POST', body: data }),
  editar: (id: number, data: { nombre: string; apellido: string }) =>
    request<Nutricionista>(`/api/nutricionistas/${id}`, { method: 'PUT', body: data }),
  eliminar: (id: number) => request<void>(`/api/nutricionistas/${id}`, { method: 'DELETE' }),
}
