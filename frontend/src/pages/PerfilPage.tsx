import { useAuth } from '../auth/useAuth'
import { card } from '../ui/styles'

export default function PerfilPage() {
  const { session, perfil } = useAuth()
  if (!session) return null

  return (
    <div className="max-w-xl">
      <h1 className="mb-4 text-2xl font-semibold">Mi perfil</h1>
      <div className={card}>
        <dl className="grid grid-cols-1 gap-4 text-sm sm:grid-cols-2">
          <div>
            <dt className="text-slate-500">Usuario</dt>
            <dd className="font-medium">{session.username}</dd>
          </div>
          <div>
            <dt className="text-slate-500">Rol</dt>
            <dd className="font-medium">{session.rol}</dd>
          </div>
          {perfil?.nombre && (
            <>
              <div>
                <dt className="text-slate-500">Nombre y apellido</dt>
                <dd className="font-medium">
                  {perfil.nombre} {perfil.apellido}
                </dd>
              </div>
              <div>
                <dt className="text-slate-500">Matrícula</dt>
                <dd className="font-medium">{perfil.matricula}</dd>
              </div>
            </>
          )}
        </dl>
        {perfil?.nombre && (
          <p className="mt-4 text-xs text-slate-500">
            Estos datos firman automáticamente cada evolución que cargues en una historia clínica.
          </p>
        )}
      </div>
    </div>
  )
}
