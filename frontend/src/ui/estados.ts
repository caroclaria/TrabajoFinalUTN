import type { EstadoCama, Sector } from '../api/types'

// Código de colores estandarizado para el estado de las camas (RNF03)
export const ESTADO_CAMA: Record<EstadoCama, { label: string; card: string; badge: string; dot: string }> = {
  DISPONIBLE: {
    label: 'Disponible',
    card: 'border-emerald-300 bg-emerald-50 hover:bg-emerald-100',
    badge: 'bg-emerald-100 text-emerald-800',
    dot: 'bg-emerald-500',
  },
  OCUPADA: {
    label: 'Ocupada',
    card: 'border-rose-300 bg-rose-50 hover:bg-rose-100',
    badge: 'bg-rose-100 text-rose-800',
    dot: 'bg-rose-500',
  },
  MANTENIMIENTO: {
    label: 'Mantenimiento',
    card: 'border-amber-300 bg-amber-50 hover:bg-amber-100',
    badge: 'bg-amber-100 text-amber-800',
    dot: 'bg-amber-500',
  },
  BLOQUEADA: {
    label: 'Bloqueada',
    card: 'border-slate-400 bg-slate-200 hover:bg-slate-300',
    badge: 'bg-slate-300 text-slate-800',
    dot: 'bg-slate-500',
  },
}

export const SECTOR_LABEL: Record<Sector, string> = {
  PEDIATRIA: 'Pediatría',
  MATERNIDAD: 'Maternidad',
  CIRUGIA: 'Cirugía',
  CLINICA: 'Clínica',
  TERAPIA: 'Terapia intensiva',
  TRAUMATOLOGIA: 'Traumatología',
  UROLOGIA: 'Urología',
}
