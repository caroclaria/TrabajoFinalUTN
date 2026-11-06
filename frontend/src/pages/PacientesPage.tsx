import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { pacientesApi } from '../api/services'
import type { Paciente } from '../api/types'
import ErrorBanner from '../components/ErrorBanner'
import PacienteFormModal from '../components/PacienteFormModal'
import { btnPrimary, cardTabla, input } from '../ui/styles'
import { formatFecha } from '../utils/fechas'
import { mensajeDe } from '../utils/errores'

export default function PacientesPage() {
  const navigate = useNavigate()
  const [pacientes, setPacientes] = useState<Paciente[]>([])
  const [soloInternados, setSoloInternados] = useState(true)
  const [busqueda, setBusqueda] = useState('')
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [creando, setCreando] = useState(false)

  useEffect(() => {
    let activo = true
    pacientesApi
      .listar(soloInternados)
      .then((lista) => {
        if (!activo) return
        setPacientes(lista)
        setError(null)
      })
      .catch((e: unknown) => {
        if (activo) setError(mensajeDe(e))
      })
      .finally(() => {
        if (activo) setCargando(false)
      })
    return () => {
      activo = false
    }
  }, [soloInternados])

  const q = busqueda.trim().toLowerCase()
  const filtrados = pacientes.filter((p) => `${p.apellido} ${p.nombre}`.toLowerCase().includes(q))

  return (
    <div>
      <div className="mb-4 flex flex-wrap items-end justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold">Pacientes</h1>
          <p className="text-sm text-slate-500">Cada paciente tiene su historia clínica nutricional.</p>
        </div>
        <button type="button" className={btnPrimary} onClick={() => setCreando(true)}>
          + Nuevo paciente
        </button>
      </div>

      <ErrorBanner mensaje={error} />

      <div className="mb-4 flex flex-wrap items-center gap-4">
        <input
          className={`${input} max-w-xs`}
          placeholder="Buscar por apellido o nombre…"
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          aria-label="Buscar paciente"
        />
        <label className="flex items-center gap-2 text-sm text-slate-700">
          <input
            type="checkbox"
            checked={soloInternados}
            onChange={(e) => setSoloInternados(e.target.checked)}
          />
          Solo internados
        </label>
      </div>

      {cargando && <p className="text-slate-500">Cargando pacientes…</p>}
      {!cargando && filtrados.length === 0 && <p className="text-slate-500">No hay pacientes para mostrar.</p>}

      {filtrados.length > 0 && (
        <div className={`${cardTabla}`}>
          <table className="min-w-full divide-y divide-slate-200 text-sm">
            <thead className="bg-slate-50 text-left text-xs uppercase tracking-wide text-slate-500">
              <tr>
                <th className="px-4 py-3">Paciente</th>
                <th className="hidden px-4 py-3 md:table-cell">Diagnóstico</th>
                <th className="px-4 py-3">Ingreso</th>
                <th className="px-4 py-3">Cama</th>
                <th className="px-4 py-3">Estado</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filtrados.map((p) => (
                <tr
                  key={p.id}
                  className="cursor-pointer hover:bg-slate-50"
                  onClick={() => navigate(`/pacientes/${p.id}`)}
                >
                  <td className="px-4 py-3 font-medium">
                    <Link to={`/pacientes/${p.id}`} className="text-emerald-700 hover:underline">
                      {p.apellido}, {p.nombre}
                    </Link>
                  </td>
                  <td className="hidden max-w-xs truncate px-4 py-3 text-slate-600 md:table-cell">
                    {p.diagnosticoBase || '—'}
                  </td>
                  <td className="px-4 py-3">{formatFecha(p.fechaIngreso)}</td>
                  <td className="px-4 py-3">{p.camaNumero ?? '—'}</td>
                  <td className="px-4 py-3">
                    {p.fechaAlta ? (
                      <span className="rounded-full bg-slate-200 px-2.5 py-0.5 text-xs font-medium text-slate-700">
                        Alta {formatFecha(p.fechaAlta)}
                      </span>
                    ) : (
                      <span className="rounded-full bg-emerald-100 px-2.5 py-0.5 text-xs font-medium text-emerald-800">
                        Internado
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {creando && (
        <PacienteFormModal
          paciente={null}
          onClose={() => setCreando(false)}
          onGuardado={(nuevo) => navigate(`/pacientes/${nuevo.id}`)}
        />
      )}
    </div>
  )
}
