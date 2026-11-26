import { useState } from 'react'
import type { FormEvent } from 'react'
import { pacientesApi } from '../api/services'
import type { Paciente } from '../api/types'
import { btnPrimary, btnSecondary, input, label } from '../ui/styles'
import { hoy } from '../utils/fechas'
import { mensajeDe } from '../utils/errores'
import ErrorBanner from './ErrorBanner'
import Modal from './Modal'

interface Props {
  paciente: Paciente | null // null = alta de un paciente nuevo; con valor = edición de datos
  onClose: () => void
  onGuardado: (p: Paciente) => void
}

export default function PacienteFormModal({ paciente, onClose, onGuardado }: Props) {
  const [nombre, setNombre] = useState(paciente?.nombre ?? '')
  const [apellido, setApellido] = useState(paciente?.apellido ?? '')
  const [diagnostico, setDiagnostico] = useState(paciente?.diagnosticoBase ?? '')
  const [fechaIngreso, setFechaIngreso] = useState(hoy())
  const [error, setError] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setEnviando(true)
    setError(null)
    try {
      const datos = { nombre: nombre.trim(), apellido: apellido.trim(), diagnosticoBase: diagnostico.trim() }
      const guardado = paciente
        ? await pacientesApi.editar(paciente.id, datos)
        : await pacientesApi.crear({ ...datos, fechaIngreso })
      onGuardado(guardado)
      onClose()
    } catch (err) {
      setError(mensajeDe(err))
      setEnviando(false)
    }
  }

  return (
    <Modal titulo={paciente ? 'Editar datos del paciente' : 'Nuevo paciente'} onClose={onClose}>
      <ErrorBanner mensaje={error} />
      <form onSubmit={onSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label htmlFor="pac-nombre" className={label}>
              Nombre
            </label>
            <input
              id="pac-nombre"
              className={input}
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              maxLength={100}
              required
            />
          </div>
          <div>
            <label htmlFor="pac-apellido" className={label}>
              Apellido
            </label>
            <input
              id="pac-apellido"
              className={input}
              value={apellido}
              onChange={(e) => setApellido(e.target.value)}
              maxLength={100}
              required
            />
          </div>
        </div>
        <div>
          <label htmlFor="pac-diag" className={label}>
            Diagnóstico médico de base
          </label>
          <textarea
            id="pac-diag"
            className={input}
            rows={3}
            value={diagnostico}
            onChange={(e) => setDiagnostico(e.target.value)}
          />
        </div>
        {!paciente && (
          <div>
            <label htmlFor="pac-ingreso" className={label}>
              Fecha de ingreso
            </label>
            <input
              id="pac-ingreso"
              type="date"
              className={input}
              value={fechaIngreso}
              onChange={(e) => setFechaIngreso(e.target.value)}
              required
            />
          </div>
        )}
        <div className="flex justify-end gap-2">
          <button type="button" className={btnSecondary} onClick={onClose}>
            Cancelar
          </button>
          <button type="submit" className={btnPrimary} disabled={enviando}>
            Guardar
          </button>
        </div>
      </form>
    </Modal>
  )
}
