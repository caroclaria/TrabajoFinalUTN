// Clases de Tailwind reutilizadas en toda la app, para que botones e inputs se vean iguales
export const btnPrimary =
  'inline-flex items-center justify-center rounded-md bg-emerald-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-50'

export const btnSecondary =
  'inline-flex items-center justify-center rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50'

export const btnDanger =
  'inline-flex items-center justify-center rounded-md bg-red-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50'

export const input =
  'block w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm shadow-sm focus:border-emerald-500 focus:outline-none focus:ring-1 focus:ring-emerald-500'

export const label = 'mb-1 block text-sm font-medium text-slate-700'

export const card = 'rounded-lg border border-slate-200 bg-white p-5 shadow-sm'

// Tarjeta para tablas: sin padding interno y con scroll horizontal propio si la tabla es ancha
export const cardTabla = 'overflow-x-auto rounded-lg border border-slate-200 bg-white shadow-sm'
