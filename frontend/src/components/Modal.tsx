import type { ReactNode } from 'react'

interface ModalProps {
  titulo: string
  onClose: () => void
  children: ReactNode
}

// Ventana emergente simple: fondo oscuro + tarjeta centrada. Click afuera o en la X para cerrar.
export default function Modal({ titulo, onClose, children }: ModalProps) {
  return (
    <div
      className="fixed inset-0 z-40 flex items-start justify-center overflow-y-auto bg-slate-900/50 p-4"
      onClick={onClose}
    >
      <div
        className="mt-10 w-full max-w-lg rounded-lg bg-white p-6 shadow-xl"
        onClick={(e) => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
        aria-label={titulo}
      >
        <div className="mb-4 flex items-start justify-between gap-4">
          <h2 className="text-lg font-semibold text-slate-900">{titulo}</h2>
          <button
            type="button"
            onClick={onClose}
            aria-label="Cerrar"
            className="rounded p-1 text-slate-500 hover:bg-slate-100 hover:text-slate-800"
          >
            ✕
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}
