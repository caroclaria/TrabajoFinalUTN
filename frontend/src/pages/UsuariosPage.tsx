import { useState } from 'react'
import NutricionistasTab from '../components/NutricionistasTab'
import UsuariosTab from '../components/UsuariosTab'

type Pestana = 'usuarios' | 'nutricionistas'

export default function UsuariosPage() {
  const [pestana, setPestana] = useState<Pestana>('usuarios')

  const clase = (p: Pestana) =>
    `border-b-2 px-4 py-2 text-sm font-medium ${
      pestana === p ? 'border-emerald-600 text-emerald-700' : 'border-transparent text-slate-500 hover:text-slate-800'
    }`

  return (
    <div>
      <h1 className="mb-4 text-2xl font-semibold">Usuarios y profesionales</h1>
      <div className="mb-6 flex border-b border-slate-200">
        <button type="button" className={clase('usuarios')} onClick={() => setPestana('usuarios')}>
          Usuarios
        </button>
        <button type="button" className={clase('nutricionistas')} onClick={() => setPestana('nutricionistas')}>
          Nutricionistas
        </button>
      </div>
      {pestana === 'usuarios' ? <UsuariosTab /> : <NutricionistasTab />}
    </div>
  )
}
