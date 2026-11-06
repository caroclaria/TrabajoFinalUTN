import { useEffect, useState } from 'react'
import { camasApi, pacientesApi } from '../api/services'
import { SECTORES, ESTADOS_CAMA } from '../api/types'
import type { Cama, Paciente } from '../api/types'
import { useAuth } from '../auth/useAuth'
import CamaFormModal from '../components/CamaFormModal'
import CamaModal from '../components/CamaModal'
import ErrorBanner from '../components/ErrorBanner'
import { ESTADO_CAMA, SECTOR_LABEL } from '../ui/estados'
import { btnPrimary, input } from '../ui/styles'
import { mensajeDe } from '../utils/errores'

export default function CamasPage() {
  const { session } = useAuth()
  const esAdmin = session?.rol === 'ADMINISTRADOR'
  const puedeGestionar = esAdmin || session?.rol === 'NUTRICIONISTA'

  const [camas, setCamas] = useState<Cama[]>([])
  const [pacientes, setPacientes] = useState<Paciente[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [version, setVersion] = useState(0) // subirlo fuerza a recargar los datos
  const [filtroPiso, setFiltroPiso] = useState('')
  const [filtroSector, setFiltroSector] = useState('')
  const [seleccionadaId, setSeleccionadaId] = useState<number | null>(null)
  const [formCama, setFormCama] = useState<{ cama: Cama | null } | null>(null)

  useEffect(() => {
    let activo = true
    Promise.all([
      camasApi.listar(),
      // Solo quien puede asignar necesita la lista de pacientes (cocina no accede a datos clínicos)
      puedeGestionar ? pacientesApi.listar(true) : Promise.resolve([] as Paciente[]),
    ])
      .then(([c, p]) => {
        if (!activo) return
        setCamas(c)
        setPacientes(p)
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
  }, [version, puedeGestionar])

  const recargar = () => setVersion((v) => v + 1)

  // --- Preparación de datos para mostrar ---
  const visibles = camas.filter(
    (c) =>
      (filtroPiso === '' || c.piso === Number(filtroPiso)) && (filtroSector === '' || c.sector === filtroSector),
  )
  const pisosDisponibles = [...new Set(camas.map((c) => c.piso))].sort((a, b) => a - b)
  const pisosVisibles = [...new Set(visibles.map((c) => c.piso))].sort((a, b) => a - b)
  const pacientesSinCama = pacientes.filter((p) => p.camaId === null)
  const seleccionada = camas.find((c) => c.id === seleccionadaId) ?? null

  return (
    <div>
      <div className="mb-4 flex flex-wrap items-end justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold">Mapa de camas</h1>
          <p className="text-sm text-slate-500">Hacé click en una cama para ver el detalle o gestionarla.</p>
        </div>
        {esAdmin && (
          <button type="button" className={btnPrimary} onClick={() => setFormCama({ cama: null })}>
            + Nueva cama
          </button>
        )}
      </div>

      <ErrorBanner mensaje={error} />

      {/* Resumen y referencias de color */}
      <div className="mb-4 flex flex-wrap gap-3">
        {ESTADOS_CAMA.map((estado) => (
          <div
            key={estado}
            className="flex items-center gap-2 rounded-md border border-slate-200 bg-white px-3 py-1.5 text-sm"
          >
            <span className={`h-3 w-3 rounded-full ${ESTADO_CAMA[estado].dot}`} />
            <span>{ESTADO_CAMA[estado].label}</span>
            <span className="font-semibold">{camas.filter((c) => c.estado === estado).length}</span>
          </div>
        ))}
      </div>

      {/* Filtros */}
      <div className="mb-6 flex flex-wrap gap-3">
        <select
          className={`${input} max-w-44`}
          value={filtroPiso}
          onChange={(e) => setFiltroPiso(e.target.value)}
          aria-label="Filtrar por piso"
        >
          <option value="">Todos los pisos</option>
          {pisosDisponibles.map((p) => (
            <option key={p} value={p}>
              Piso {p}
            </option>
          ))}
        </select>
        <select
          className={`${input} max-w-56`}
          value={filtroSector}
          onChange={(e) => setFiltroSector(e.target.value)}
          aria-label="Filtrar por sector"
        >
          <option value="">Todos los sectores</option>
          {SECTORES.map((s) => (
            <option key={s} value={s}>
              {SECTOR_LABEL[s]}
            </option>
          ))}
        </select>
      </div>

      {cargando && <p className="text-slate-500">Cargando camas…</p>}
      {!cargando && visibles.length === 0 && (
        <p className="text-slate-500">
          No hay camas para mostrar.{esAdmin ? ' Podés crear la primera con "Nueva cama".' : ''}
        </p>
      )}

      {/* Un bloque por piso y, dentro, un grupo por sector */}
      {pisosVisibles.map((piso) => {
        const camasDelPiso = visibles.filter((c) => c.piso === piso)
        const sectores = [...new Set(camasDelPiso.map((c) => c.sector))]
        return (
          <section key={piso} className="mb-8">
            <h2 className="mb-3 border-b border-slate-200 pb-1 text-lg font-semibold">Piso {piso}</h2>
            {sectores.map((sector) => (
              <div key={sector} className="mb-4">
                <h3 className="mb-2 text-sm font-medium uppercase tracking-wide text-slate-500">
                  {SECTOR_LABEL[sector]}
                </h3>
                <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6">
                  {camasDelPiso
                    .filter((c) => c.sector === sector)
                    .map((c) => (
                      <button
                        key={c.id}
                        type="button"
                        onClick={() => setSeleccionadaId(c.id)}
                        className={`rounded-lg border p-3 text-left transition-colors ${ESTADO_CAMA[c.estado].card}`}
                      >
                        <span className="block text-lg font-semibold">{c.numero}</span>
                        <span className="block text-xs text-slate-600">{ESTADO_CAMA[c.estado].label}</span>
                        {c.pacienteNombre && (
                          <span className="mt-1 block truncate text-xs font-medium text-slate-800">
                            {c.pacienteNombre}
                          </span>
                        )}
                      </button>
                    ))}
                </div>
              </div>
            ))}
          </section>
        )
      })}

      {seleccionada && (
        <CamaModal
          cama={seleccionada}
          pacientesSinCama={pacientesSinCama}
          puedeGestionar={puedeGestionar}
          esAdmin={esAdmin}
          onClose={() => setSeleccionadaId(null)}
          onCambio={recargar}
          onEditar={() => {
            setFormCama({ cama: seleccionada })
            setSeleccionadaId(null)
          }}
        />
      )}

      {formCama && (
        <CamaFormModal cama={formCama.cama} onClose={() => setFormCama(null)} onGuardado={recargar} />
      )}
    </div>
  )
}
