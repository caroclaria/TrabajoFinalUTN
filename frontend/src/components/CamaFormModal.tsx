import { useState } from 'react'
import type { FormEvent } from 'react'
import { camasApi } from '../api/services'
import { SECTORES } from '../api/types'
import type { Cama, Sector } from '../api/types'
import { SECTOR_LABEL } from '../ui/estados'
import { btnPrimary, btnSecondary, input, label } from '../ui/styles'
import { mensajeDe } from '../utils/errores'
import ErrorBanner from './ErrorBanner'
import Modal from './Modal'

interface Props {
  cama: Cama | null // null = crear una cama nueva; con valor = editarla
  onClose: () => void
  onGuardado: () => void
}

export default function CamaFormModal({ cama, onClose, onGuardado }: Props) {
  const [numero, setNumero] = useState(cama?.numero ?? '')
  const [piso, setPiso] = useState(cama ? String(cama.piso) : '1')
  const [sector, setSector] = useState<Sector>(cama?.sector ?? 'CLINICA')
  const [error, setError] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setEnviando(true)
    setError(null)
    const datos = { numero: numero.trim(), piso: Number(piso), sector }
    try {
      if (cama) await camasApi.editar(cama.id, datos)
      else await camasApi.crear(datos)
      onGuardado()
      onClose()
    } catch (err) {
      setError(mensajeDe(err))
      setEnviando(false)
    }
  }

  return (
    <Modal titulo={cama ? `Editar cama ${cama.numero}` : 'Nueva cama'} onClose={onClose}>
      <ErrorBanner mensaje={error} />
      <form onSubmit={onSubmit} className="space-y-4">
        <div>
          <label htmlFor="cama-numero" className={label}>
            Número
          </label>
          <input
            id="cama-numero"
            className={input}
            value={numero}
            onChange={(e) => setNumero(e.target.value)}
            maxLength={20}
            required
          />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label htmlFor="cama-piso" className={label}>
              Piso
            </label>
            <input
              id="cama-piso"
              type="number"
              min={0}
              className={input}
              value={piso}
              onChange={(e) => setPiso(e.target.value)}
              required
            />
          </div>
          <div>
            <label htmlFor="cama-sector" className={label}>
              Sector
            </label>
            <select
              id="cama-sector"
              className={input}
              value={sector}
              onChange={(e) => setSector(e.target.value as Sector)}
            >
              {SECTORES.map((s) => (
                <option key={s} value={s}>
                  {SECTOR_LABEL[s]}
                </option>
              ))}
            </select>
          </div>
        </div>
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
