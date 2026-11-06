// Saca un mensaje legible de cualquier error capturado en un catch
export function mensajeDe(e: unknown): string {
  return e instanceof Error ? e.message : 'Ocurrió un error inesperado'
}
