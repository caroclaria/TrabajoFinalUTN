import type { Rol } from './types'

// Cliente HTTP único: todas las llamadas a la API pasan por acá.
// Se encarga de poner la URL base, el token JWT, parsear el JSON y traducir los errores.

const API_URL: string = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

const SESSION_KEY = 'tpfinal.session'

export interface StoredSession {
  token: string
  username: string
  rol: Rol
}

export function loadSession(): StoredSession | null {
  try {
    const raw = localStorage.getItem(SESSION_KEY)
    return raw ? (JSON.parse(raw) as StoredSession) : null
  } catch {
    return null
  }
}

export function saveSession(session: StoredSession): void {
  localStorage.setItem(SESSION_KEY, JSON.stringify(session))
}

export function clearSession(): void {
  localStorage.removeItem(SESSION_KEY)
}

export class ApiError extends Error {
  status: number

  constructor(message: string, status: number) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

// El AuthProvider registra acá qué hacer cuando el servidor responde 401 (token vencido o inválido)
let onUnauthorized: (() => void) | null = null
export function setUnauthorizedHandler(handler: (() => void) | null): void {
  onUnauthorized = handler
}

interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'
  body?: unknown
  auth?: boolean // false solo para el login
}

// El backend responde {"error": "..."} o, en validaciones, {"campo": "mensaje", ...}.
// Los errores "genéricos" de Spring traen un campo numérico "status": esos los ignoramos
// y usamos nuestro propio mensaje según el código HTTP.
async function extraerMensaje(response: Response): Promise<string> {
  try {
    const data: unknown = await response.json()
    if (data && typeof data === 'object' && typeof (data as Record<string, unknown>).status !== 'number') {
      const objeto = data as Record<string, unknown>
      if (typeof objeto.error === 'string') return objeto.error
      const mensajes = Object.values(objeto).filter((v): v is string => typeof v === 'string')
      if (mensajes.length > 0) return mensajes.join('. ')
    }
  } catch {
    // el cuerpo no era JSON: usamos el mensaje por defecto de abajo
  }
  if (response.status === 403) return 'No tenés permisos para realizar esta acción'
  if (response.status === 401) return 'Tu sesión venció, volvé a iniciar sesión'
  return 'Ocurrió un error inesperado'
}

export async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = 'GET', body, auth = true } = options

  const headers: Record<string, string> = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (auth) {
    const session = loadSession()
    if (session) headers.Authorization = `Bearer ${session.token}`
  }

  let response: Response
  try {
    response = await fetch(`${API_URL}${path}`, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new ApiError('No se pudo conectar con el servidor', 0)
  }

  if (!response.ok) {
    if (response.status === 401 && auth && onUnauthorized) onUnauthorized()
    throw new ApiError(await extraerMensaje(response), response.status)
  }

  if (response.status === 204) return undefined as T
  return (await response.json()) as T
}
