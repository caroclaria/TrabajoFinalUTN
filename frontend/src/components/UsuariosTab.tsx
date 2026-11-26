import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { usuariosApi } from '../api/services'
import type { Rol, Usuario } from '../api/types'
import { useAuth } from '../auth/useAuth'
import { btnDanger, btnPrimary, btnSecondary, card, cardTabla, input, label } from '../ui/styles'
import { mensajeDe } from '../utils/errores'
import ErrorBanner from './ErrorBanner'

// Los nutricionistas se crean desde su propia pestaña (necesitan nombre, apellido y matrícula)
const ROLES_CREABLES: Rol[] = ['ADMINISTRADOR', 'COCINA']

export default function UsuariosTab() {
  const { session } = useAuth()
  const [usuarios, setUsuarios] = useState<Usuario[]>([])
  const [error, setError] = useState<string | null>(null)
  const [version, setVersion] = useState(0)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [rol, setRol] = useState<Rol>('COCINA')

  useEffect(() => {
    let activo = true
    usuariosApi
      .listar()
      .then((lista) => {
        if (activo) setUsuarios(lista)
      })
      .catch((e: unknown) => {
        if (activo) setError(mensajeDe(e))
      })
    return () => {
      activo = false
    }
  }, [version])

  const recargar = () => setVersion((v) => v + 1)

  async function ejecutar(accion: () => Promise<unknown>) {
    setError(null)
    try {
      await accion()
      recargar()
    } catch (e) {
      setError(mensajeDe(e))
    }
  }

  async function crear(e: FormEvent) {
    e.preventDefault()
    await ejecutar(async () => {
      await usuariosApi.crear({ username: username.trim(), password, rol })
      setUsername('')
      setPassword('')
    })
  }

  return (
    <div>
      <ErrorBanner mensaje={error} />

      <form onSubmit={crear} className={`${card} mb-6`}>
        <h2 className="mb-3 font-semibold">Nuevo usuario (administrador o cocina)</h2>
        <div className="grid gap-4 sm:grid-cols-4">
          <div>
            <label htmlFor="u-username" className={label}>
              Usuario
            </label>
            <input
              id="u-username"
              className={input}
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              maxLength={50}
              required
            />
          </div>
          <div>
            <label htmlFor="u-password" className={label}>
              Contraseña
            </label>
            <input
              id="u-password"
              type="password"
              className={input}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              minLength={6}
              required
            />
          </div>
          <div>
            <label htmlFor="u-rol" className={label}>
              Rol
            </label>
            <select id="u-rol" className={input} value={rol} onChange={(e) => setRol(e.target.value as Rol)}>
              {ROLES_CREABLES.map((r) => (
                <option key={r} value={r}>
                  {r}
                </option>
              ))}
            </select>
          </div>
          <div className="flex items-end">
            <button type="submit" className={`${btnPrimary} w-full`}>
              Crear
            </button>
          </div>
        </div>
      </form>

      <div className={`${cardTabla}`}>
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50 text-left text-xs uppercase tracking-wide text-slate-500">
            <tr>
              <th className="px-4 py-3">Usuario</th>
              <th className="px-4 py-3">Rol</th>
              <th className="px-4 py-3">Estado</th>
              <th className="px-4 py-3 text-right">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {usuarios.map((u) => {
              const esYo = u.username === session?.username
              return (
                <tr key={u.id}>
                  <td className="px-4 py-3 font-medium">
                    {u.username} {esYo && <span className="text-xs text-slate-400">(vos)</span>}
                  </td>
                  <td className="px-4 py-3">{u.rol}</td>
                  <td className="px-4 py-3">{u.activo ? 'Activo' : 'Inactivo'}</td>
                  <td className="px-4 py-3">
                    <div className="flex justify-end gap-2">
                      <button
                        type="button"
                        className={btnSecondary}
                        disabled={esYo}
                        onClick={() => ejecutar(() => usuariosApi.editar(u.id, { activo: !u.activo }))}
                      >
                        {u.activo ? 'Desactivar' : 'Activar'}
                      </button>
                      <button
                        type="button"
                        className={btnDanger}
                        disabled={esYo || u.rol === 'NUTRICIONISTA'}
                        title={u.rol === 'NUTRICIONISTA' ? 'Se gestiona desde la pestaña Nutricionistas' : undefined}
                        onClick={() => {
                          if (window.confirm(`¿Eliminar al usuario ${u.username}?`)) {
                            void ejecutar(() => usuariosApi.eliminar(u.id))
                          }
                        }}
                      >
                        Eliminar
                      </button>
                    </div>
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>
    </div>
  )
}
