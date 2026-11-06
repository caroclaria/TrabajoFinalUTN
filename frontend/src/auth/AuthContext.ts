import { createContext } from 'react'
import type { Perfil } from '../api/types'
import type { StoredSession } from '../api/client'

export interface AuthContextValue {
  session: StoredSession | null
  perfil: Perfil | null
  login: (username: string, password: string) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthContextValue | null>(null)
