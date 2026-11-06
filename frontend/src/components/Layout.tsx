import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'

const ROL_LABEL = {
  ADMINISTRADOR: 'Administrador',
  NUTRICIONISTA: 'Nutricionista',
  COCINA: 'Cocina',
} as const

export default function Layout() {
  const { session, perfil, logout } = useAuth()
  if (!session) return null

  const enlaces = [
    { to: '/camas', texto: 'Camas', visible: true },
    { to: '/pacientes', texto: 'Pacientes', visible: session.rol !== 'COCINA' },
    { to: '/usuarios', texto: 'Usuarios', visible: session.rol === 'ADMINISTRADOR' },
  ].filter((e) => e.visible)

  const nombreVisible = perfil?.nombre ? `${perfil.nombre} ${perfil.apellido}` : session.username

  return (
    <div className="min-h-screen">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-6xl flex-wrap items-center justify-between gap-3 px-4 py-3">
          <div className="flex flex-wrap items-center gap-6">
            <span className="text-base font-semibold text-emerald-700">UxS · Dietoterapia</span>
            <nav className="flex gap-1">
              {enlaces.map((e) => (
                <NavLink
                  key={e.to}
                  to={e.to}
                  className={({ isActive }) =>
                    `rounded-md px-3 py-1.5 text-sm font-medium ${
                      isActive ? 'bg-emerald-100 text-emerald-800' : 'text-slate-600 hover:bg-slate-100'
                    }`
                  }
                >
                  {e.texto}
                </NavLink>
              ))}
            </nav>
          </div>
          <div className="flex items-center gap-3 text-sm">
            <NavLink to="/perfil" className="text-right text-slate-700 hover:text-emerald-700">
              <span className="block font-medium leading-tight">{nombreVisible}</span>
              <span className="block text-xs text-slate-500">{ROL_LABEL[session.rol]}</span>
            </NavLink>
            <button
              type="button"
              onClick={logout}
              className="rounded-md border border-slate-300 px-3 py-1.5 text-slate-700 hover:bg-slate-50"
            >
              Salir
            </button>
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-6xl px-4 py-6">
        <Outlet />
      </main>
    </div>
  )
}
