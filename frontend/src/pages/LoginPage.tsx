import { useState } from 'react'
import type { FormEvent } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import ErrorBanner from '../components/ErrorBanner'
import { useAuth } from '../auth/useAuth'
import { btnPrimary, card, input, label } from '../ui/styles'
import { mensajeDe } from '../utils/errores'

export default function LoginPage() {
  const { session, login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)

  // Si ya hay sesión no tiene sentido mostrar el login
  if (session) return <Navigate to="/camas" replace />

  // Si el usuario venía de una página protegida, lo devolvemos ahí después de loguearse
  const destino = (location.state as { from?: string } | null)?.from ?? '/camas'

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setEnviando(true)
    setError(null)
    try {
      await login(username.trim(), password)
      navigate(destino, { replace: true })
    } catch (err) {
      setError(mensajeDe(err))
      setEnviando(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className={`${card} w-full max-w-sm`}>
        <h1 className="text-xl font-semibold text-emerald-700">Sanatorio Unidos por la Salud</h1>
        <p className="mb-5 text-sm text-slate-500">Gestión y trazabilidad dietoterápica</p>

        <ErrorBanner mensaje={error} />

        <form onSubmit={onSubmit} className="space-y-4">
          <div>
            <label htmlFor="username" className={label}>
              Usuario
            </label>
            <input
              id="username"
              className={input}
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
              required
              autoFocus
            />
          </div>
          <div>
            <label htmlFor="password" className={label}>
              Contraseña
            </label>
            <input
              id="password"
              type="password"
              className={input}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              required
            />
          </div>
          <button type="submit" className={`${btnPrimary} w-full`} disabled={enviando}>
            {enviando ? 'Ingresando…' : 'Ingresar'}
          </button>
        </form>
      </div>
    </div>
  )
}
