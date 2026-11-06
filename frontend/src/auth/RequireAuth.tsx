import type { ReactNode } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import type { Rol } from '../api/types'
import { useAuth } from './useAuth'

// Protege una ruta: sin sesión → login; con sesión pero sin el rol necesario → panel de camas.
export default function RequireAuth({ roles, children }: { roles?: Rol[]; children: ReactNode }) {
  const { session } = useAuth()
  const location = useLocation()

  if (!session) return <Navigate to="/login" replace state={{ from: location.pathname }} />
  if (roles && !roles.includes(session.rol)) return <Navigate to="/camas" replace />
  return <>{children}</>
}
