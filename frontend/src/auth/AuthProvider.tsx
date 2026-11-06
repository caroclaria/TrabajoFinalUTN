import { useCallback, useEffect, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import { clearSession, loadSession, saveSession, setUnauthorizedHandler } from '../api/client'
import type { StoredSession } from '../api/client'
import { authApi } from '../api/services'
import type { Perfil } from '../api/types'
import { AuthContext } from './AuthContext'

export default function AuthProvider({ children }: { children: ReactNode }) {
  // La sesión se recupera de localStorage, así sobrevive a recargar la página
  const [session, setSession] = useState<StoredSession | null>(() => loadSession())
  const [perfilCargado, setPerfilCargado] = useState<Perfil | null>(null)

  const logout = useCallback(() => {
    clearSession()
    setSession(null)
    setPerfilCargado(null)
  }, [])

  // Si el backend responde 401 (token vencido), cerramos la sesión y el router manda al login
  useEffect(() => {
    setUnauthorizedHandler(logout)
    return () => setUnauthorizedHandler(null)
  }, [logout])

  // Cada vez que hay una sesión nueva, traemos el perfil (nombre, apellido, matrícula)
  const token = session?.token
  useEffect(() => {
    if (!token) return
    let activo = true
    authApi
      .me()
      .then((p) => {
        if (activo) setPerfilCargado(p)
      })
      .catch(() => {
        // si falla, el perfil queda vacío; el 401 ya lo maneja el cliente HTTP
      })
    return () => {
      activo = false
    }
  }, [token])

  const login = useCallback(async (username: string, password: string) => {
    const respuesta = await authApi.login(username, password)
    const nueva: StoredSession = { token: respuesta.token, username: respuesta.username, rol: respuesta.rol }
    saveSession(nueva)
    setSession(nueva)
  }, [])

  const value = useMemo(
    () => ({ session, perfil: session ? perfilCargado : null, login, logout }),
    [session, perfilCargado, login, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
