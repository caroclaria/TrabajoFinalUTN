import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useParams } from 'react-router-dom'
import { pacientesApi } from '../api/services'
import type { Evolucion, Paciente } from '../api/types'
import { useAuth } from '../auth/useAuth'
import ErrorBanner from '../components/ErrorBanner'
import PacienteFormModal from '../components/PacienteFormModal'
import { btnPrimary, btnSecondary, card, input, label } from '../ui/styles'
import { formatFecha, formatFechaHora } from '../utils/fechas'
import { mensajeDe } from '../utils/errores'

export default function PacienteDetallePage() {
  const { id } = useParams()
  const pacienteId = Number(id)
  const { session, perfil } = useAuth()
  const esNutricionista = session?.rol === 'NUTRICIONISTA'

  const [paciente, setPaciente] = useState<Paciente | null>(null)
  const [evoluciones, setEvoluciones] = useState<Evolucion[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [version, setVersion] = useState(0)
  const [editando, setEditando] = useState(false)
  const [nota, setNota] = useState('')
  const [guardando, setGuardando] = useState(false)
  const [errorNota, setErrorNota] = useState<string | null>(null)

  useEffect(() => {
    let activo = true
    Promise.all([pacientesApi.obtener(pacienteId), pacientesApi.evoluciones(pacienteId)])
      .then(([p, ev]) => {
        if (!activo) return
        setPaciente(p)
        setEvoluciones(ev)
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
  }, [pacienteId, version])

  const recargar = () => setVersion((v) => v + 1)

  async function guardarEvolucion(e: FormEvent) {
    e.preventDefault()
    setGuardando(true)
    setErrorNota(null)
    try {
      await pacientesApi.crearEvolucion(pacienteId, nota.trim())
      setNota('')
      recargar()
    } catch (err) {
      setErrorNota(mensajeDe(err))
    } finally {
      setGuardando(false)
    }
  }

  async function darDeAlta() {
    if (!paciente) return
    const aviso = paciente.camaNumero
      ? `Se registrará el alta y se liberará la cama ${paciente.camaNumero}. ¿Continuar?`
      : 'Se registrará el alta del paciente. ¿Continuar?'
    if (!window.confirm(aviso)) return
    try {
      await pacientesApi.darAlta(paciente.id)
      recargar()
    } catch (err) {
      setError(mensajeDe(err))
    }
  }

  if (cargando) return <p className="text-slate-500">Cargando historia clínica…</p>
  if (!paciente) {
    return (
      <div>
        <ErrorBanner mensaje={error ?? 'Paciente no encontrado'} />
        <Link to="/pacientes" className="text-emerald-700 hover:underline">
          ← Volver a pacientes
        </Link>
      </div>
    )
  }

  const internado = paciente.fechaAlta === null
  // Más reciente primero para leer cómodo; el backend las devuelve en orden cronológico
  const evolucionesRecientes = [...evoluciones].reverse()

  return (
    <div>
      <Link to="/pacientes" className="text-sm text-emerald-700 hover:underline">
        ← Volver a pacientes
      </Link>

      <ErrorBanner mensaje={error} />

      {/* Datos filiatorios y diagnóstico de base */}
      <section className={`${card} mt-3`}>
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <h1 className="text-2xl font-semibold">
              {paciente.apellido}, {paciente.nombre}
            </h1>
            <p className="text-sm text-slate-500">Historia clínica N.º {paciente.historiaClinicaId}</p>
          </div>
          <div className="flex gap-2">
            <button type="button" className={btnSecondary} onClick={() => setEditando(true)}>
              Editar datos
            </button>
            {internado && (
              <button type="button" className={btnSecondary} onClick={darDeAlta}>
                Registrar alta
              </button>
            )}
          </div>
        </div>

        <dl className="mt-4 grid grid-cols-1 gap-4 text-sm sm:grid-cols-3">
          <div>
            <dt className="text-slate-500">Fecha de ingreso</dt>
            <dd className="font-medium">{formatFecha(paciente.fechaIngreso)}</dd>
          </div>
          <div>
            <dt className="text-slate-500">Cama actual</dt>
            <dd className="font-medium">{paciente.camaNumero ?? 'Sin cama asignada'}</dd>
          </div>
          <div>
            <dt className="text-slate-500">Estado</dt>
            <dd className="font-medium">{internado ? 'Internado' : `Alta el ${formatFecha(paciente.fechaAlta)}`}</dd>
          </div>
        </dl>

        <div className="mt-4 text-sm">
          <p className="text-slate-500">Diagnóstico médico de base</p>
          <p className="whitespace-pre-wrap">{paciente.diagnosticoBase || 'Sin diagnóstico registrado'}</p>
        </div>
      </section>

      {/* Evoluciones */}
      <section className="mt-6">
        <h2 className="mb-1 text-lg font-semibold">Evoluciones nutricionales</h2>
        <p className="mb-4 text-sm text-slate-500">
          Registro inmutable: una vez firmada, una evolución no se puede editar ni eliminar.
        </p>

        {esNutricionista && internado && (
          <form onSubmit={guardarEvolucion} className={`${card} mb-6`}>
            <ErrorBanner mensaje={errorNota} />
            <label htmlFor="nota" className={label}>
              Nueva evolución
            </label>
            <textarea
              id="nota"
              className={input}
              rows={4}
              value={nota}
              onChange={(e) => setNota(e.target.value)}
              placeholder="Describí la evolución nutricional del paciente…"
              required
            />
            <div className="mt-3 flex flex-wrap items-center justify-between gap-3">
              <p className="text-xs text-slate-500">
                Se firmará como{' '}
                {perfil?.nombre ? `${perfil.nombre} ${perfil.apellido} (Mat. ${perfil.matricula})` : 'tu usuario'} con la
                fecha y hora actuales.
              </p>
              <button type="submit" className={btnPrimary} disabled={guardando || nota.trim() === ''}>
                Firmar y guardar
              </button>
            </div>
          </form>
        )}

        {evolucionesRecientes.length === 0 ? (
          <p className="text-slate-500">Todavía no hay evoluciones registradas.</p>
        ) : (
          <ol className="space-y-3">
            {evolucionesRecientes.map((ev) => (
              <li key={ev.id} className={card}>
                <div className="mb-2 flex flex-wrap items-center justify-between gap-2 text-sm">
                  <span className="font-medium text-slate-800">{formatFechaHora(ev.fechaHora)}</span>
                  <span className="text-slate-500">
                    Lic. {ev.nutricionistaApellido}, {ev.nutricionistaNombre} · Mat. {ev.nutricionistaMatricula}
                  </span>
                </div>
                <p className="whitespace-pre-wrap text-sm">{ev.nota}</p>
              </li>
            ))}
          </ol>
        )}
      </section>

      {editando && (
        <PacienteFormModal paciente={paciente} onClose={() => setEditando(false)} onGuardado={recargar} />
      )}
    </div>
  )
}
