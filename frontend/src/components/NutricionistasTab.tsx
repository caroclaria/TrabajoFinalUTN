import { useEffect, useState } from 'react'
import type { ChangeEvent, FormEvent } from 'react'
import { nutricionistasApi } from '../api/services'
import type { Nutricionista } from '../api/types'
import { btnDanger, btnPrimary, btnSecondary, card, cardTabla, input, label } from '../ui/styles'
import { mensajeDe } from '../utils/errores'
import ErrorBanner from './ErrorBanner'
import Modal from './Modal'

const vacio = { username: '', password: '', nombre: '', apellido: '', matricula: '' }

export default function NutricionistasTab() {
  const [lista, setLista] = useState<Nutricionista[]>([])
  const [error, setError] = useState<string | null>(null)
  const [version, setVersion] = useState(0)
  const [form, setForm] = useState(vacio)
  const [editando, setEditando] = useState<Nutricionista | null>(null)
  const [editNombre, setEditNombre] = useState('')
  const [editApellido, setEditApellido] = useState('')

  useEffect(() => {
    let activo = true
    nutricionistasApi
      .listar()
      .then((l) => {
        if (activo) setLista(l)
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
      await nutricionistasApi.crear({
        username: form.username.trim(),
        password: form.password,
        nombre: form.nombre.trim(),
        apellido: form.apellido.trim(),
        matricula: form.matricula.trim(),
      })
      setForm(vacio)
    })
  }

  function abrirEdicion(n: Nutricionista) {
    setEditando(n)
    setEditNombre(n.nombre)
    setEditApellido(n.apellido)
  }

  async function guardarEdicion(e: FormEvent) {
    e.preventDefault()
    if (!editando) return
    await ejecutar(() => nutricionistasApi.editar(editando.id, { nombre: editNombre.trim(), apellido: editApellido.trim() }))
    setEditando(null)
  }

  const campo = (clave: keyof typeof vacio) => ({
    value: form[clave],
    onChange: (e: ChangeEvent<HTMLInputElement>) => setForm({ ...form, [clave]: e.target.value }),
  })

  return (
    <div>
      <ErrorBanner mensaje={error} />

      <form onSubmit={crear} className={`${card} mb-6`}>
        <h2 className="mb-3 font-semibold">Nuevo nutricionista</h2>
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <div>
            <label htmlFor="n-nombre" className={label}>
              Nombre
            </label>
            <input id="n-nombre" className={input} required {...campo('nombre')} />
          </div>
          <div>
            <label htmlFor="n-apellido" className={label}>
              Apellido
            </label>
            <input id="n-apellido" className={input} required {...campo('apellido')} />
          </div>
          <div>
            <label htmlFor="n-matricula" className={label}>
              Matrícula
            </label>
            <input id="n-matricula" className={input} maxLength={30} required {...campo('matricula')} />
          </div>
          <div>
            <label htmlFor="n-username" className={label}>
              Usuario
            </label>
            <input id="n-username" className={input} maxLength={50} required {...campo('username')} />
          </div>
          <div>
            <label htmlFor="n-password" className={label}>
              Contraseña
            </label>
            <input id="n-password" type="password" className={input} minLength={6} required {...campo('password')} />
          </div>
          <div className="flex items-end">
            <button type="submit" className={`${btnPrimary} w-full`}>
              Crear nutricionista
            </button>
          </div>
        </div>
      </form>

      <div className={`${cardTabla}`}>
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50 text-left text-xs uppercase tracking-wide text-slate-500">
            <tr>
              <th className="px-4 py-3">Profesional</th>
              <th className="px-4 py-3">Matrícula</th>
              <th className="px-4 py-3 text-right">Acciones</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {lista.map((n) => (
              <tr key={n.id}>
                <td className="px-4 py-3 font-medium">
                  {n.apellido}, {n.nombre}
                </td>
                <td className="px-4 py-3">{n.matricula}</td>
                <td className="px-4 py-3">
                  <div className="flex justify-end gap-2">
                    <button type="button" className={btnSecondary} onClick={() => abrirEdicion(n)}>
                      Editar
                    </button>
                    <button
                      type="button"
                      className={btnDanger}
                      onClick={() => {
                        if (window.confirm(`¿Eliminar a ${n.apellido}, ${n.nombre}?`)) {
                          void ejecutar(() => nutricionistasApi.eliminar(n.id))
                        }
                      }}
                    >
                      Eliminar
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {editando && (
        <Modal titulo="Editar nutricionista" onClose={() => setEditando(null)}>
          <form onSubmit={guardarEdicion} className="space-y-4">
            <div>
              <label htmlFor="e-nombre" className={label}>
                Nombre
              </label>
              <input
                id="e-nombre"
                className={input}
                value={editNombre}
                onChange={(e) => setEditNombre(e.target.value)}
                required
              />
            </div>
            <div>
              <label htmlFor="e-apellido" className={label}>
                Apellido
              </label>
              <input
                id="e-apellido"
                className={input}
                value={editApellido}
                onChange={(e) => setEditApellido(e.target.value)}
                required
              />
            </div>
            <p className="text-xs text-slate-500">La matrícula y el usuario no se modifican desde acá.</p>
            <div className="flex justify-end gap-2">
              <button type="button" className={btnSecondary} onClick={() => setEditando(null)}>
                Cancelar
              </button>
              <button type="submit" className={btnPrimary}>
                Guardar
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  )
}
