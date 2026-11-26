import { useState } from 'react'
import { Link } from 'react-router-dom'
import { camasApi } from '../api/services'
import type { Cama, EstadoCama, Paciente } from '../api/types'
import { ESTADO_CAMA, SECTOR_LABEL } from '../ui/estados'
import { btnDanger, btnPrimary, btnSecondary, input, label } from '../ui/styles'
import { mensajeDe } from '../utils/errores'
import ErrorBanner from './ErrorBanner'
import Modal from './Modal'

interface Props {
  cama: Cama
  pacientesSinCama: Paciente[]
  puedeGestionar: boolean // administrador o nutricionista
  esAdmin: boolean
  onClose: () => void
  onCambio: () => void // pide a la página que vuelva a cargar las camas
  onEditar: () => void
}

const ESTADOS_MANUALES: EstadoCama[] = ['DISPONIBLE', 'MANTENIMIENTO', 'BLOQUEADA']

export default function CamaModal({
  cama,
  pacientesSinCama,
  puedeGestionar,
  esAdmin,
  onClose,
  onCambio,
  onEditar,
}: Props) {
  const [pacienteId, setPacienteId] = useState('')
  const [ocupado, setOcupado] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Ejecuta una acción de la API, y refresca el panel siempre (incluso si falló, por si otra persona
  // modificó la cama al mismo tiempo y los datos en pantalla quedaron viejos)
  async function ejecutar(accion: () => Promise<unknown>, cerrar = true) {
    setOcupado(true)
    setError(null)
    try {
      await accion()
      onCambio()
      if (cerrar) onClose()
      else setOcupado(false)
    } catch (e) {
      setError(mensajeDe(e))
      setOcupado(false)
      onCambio()
    }
  }

  const estilo = ESTADO_CAMA[cama.estado]
  const tienePaciente = cama.pacienteId !== null

  return (
    <Modal titulo={`Cama ${cama.numero}`} onClose={onClose}>
      <ErrorBanner mensaje={error} />

      <div className="mb-4 flex flex-wrap items-center gap-2 text-sm text-slate-600">
        <span className={`rounded-full px-2.5 py-0.5 text-xs font-medium ${estilo.badge}`}>{estilo.label}</span>
        <span>
          Piso {cama.piso} · {SECTOR_LABEL[cama.sector]}
        </span>
      </div>

      {tienePaciente && (
        <div className="mb-4 rounded-md border border-slate-200 bg-slate-50 p-3 text-sm">
          <p className="text-slate-500">Paciente internado</p>
          <p className="font-medium text-slate-900">{cama.pacienteNombre}</p>
          {puedeGestionar && (
            <Link to={`/pacientes/${cama.pacienteId}`} className="text-emerald-700 hover:underline">
              Ver historia clínica
            </Link>
          )}
        </div>
      )}

      {puedeGestionar && tienePaciente && (
        <button
          type="button"
          className={`${btnSecondary} mb-4 w-full`}
          disabled={ocupado}
          onClick={() => ejecutar(() => camasApi.desasignar(cama.id))}
        >
          Desasignar paciente (libera la cama)
        </button>
      )}

      {puedeGestionar && cama.estado === 'DISPONIBLE' && (
        <div className="mb-4 rounded-md border border-slate-200 p-3">
          <label htmlFor="asignar-paciente" className={label}>
            Asignar paciente
          </label>
          {pacientesSinCama.length === 0 ? (
            <p className="text-sm text-slate-500">No hay pacientes internados sin cama.</p>
          ) : (
            <div className="flex gap-2">
              <select
                id="asignar-paciente"
                className={input}
                value={pacienteId}
                onChange={(e) => setPacienteId(e.target.value)}
              >
                <option value="">Elegí un paciente…</option>
                {pacientesSinCama.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.apellido}, {p.nombre}
                  </option>
                ))}
              </select>
              <button
                type="button"
                className={btnPrimary}
                disabled={ocupado || pacienteId === ''}
                onClick={() => ejecutar(() => camasApi.asignar(cama.id, Number(pacienteId)))}
              >
                Asignar
              </button>
            </div>
          )}
        </div>
      )}

      {puedeGestionar && !tienePaciente && (
        <div className="mb-4">
          <p className={label}>Cambiar estado</p>
          <div className="flex flex-wrap gap-2">
            {ESTADOS_MANUALES.filter((e) => e !== cama.estado).map((e) => (
              <button
                key={e}
                type="button"
                className={btnSecondary}
                disabled={ocupado}
                onClick={() => ejecutar(() => camasApi.cambiarEstado(cama.id, e))}
              >
                Marcar {ESTADO_CAMA[e].label.toLowerCase()}
              </button>
            ))}
          </div>
        </div>
      )}

      {esAdmin && (
        <div className="flex justify-between border-t border-slate-200 pt-4">
          <button type="button" className={btnSecondary} onClick={onEditar}>
            Editar cama
          </button>
          <button
            type="button"
            className={btnDanger}
            disabled={ocupado || tienePaciente}
            title={tienePaciente ? 'No se puede eliminar una cama ocupada' : undefined}
            onClick={() => {
              if (window.confirm(`¿Eliminar la cama ${cama.numero}?`)) {
                void ejecutar(() => camasApi.eliminar(cama.id))
              }
            }}
          >
            Eliminar
          </button>
        </div>
      )}
    </Modal>
  )
}
