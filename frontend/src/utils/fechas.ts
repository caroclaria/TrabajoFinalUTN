// "2026-11-04" → "04/11/2026". Se arma a mano (sin new Date) para evitar el corrimiento de un día por zona horaria.
export function formatFecha(iso: string | null): string {
  if (!iso) return '—'
  const [anio, mes, dia] = iso.split('T')[0].split('-')
  return `${dia}/${mes}/${anio}`
}

// "2026-11-04T10:15:30" → "04/11/2026 10:15"
export function formatFechaHora(iso: string): string {
  // slice(0, 19) descarta los decimales de segundo (el backend manda microsegundos)
  const fecha = new Date(iso.slice(0, 19))
  return fecha.toLocaleString('es-AR', { dateStyle: 'short', timeStyle: 'short' })
}

// Fecha de hoy en formato yyyy-mm-dd, para el valor inicial de los <input type="date">
export function hoy(): string {
  const d = new Date()
  const mes = String(d.getMonth() + 1).padStart(2, '0')
  const dia = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${mes}-${dia}`
}
