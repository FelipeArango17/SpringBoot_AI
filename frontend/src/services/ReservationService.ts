import axios from 'axios'
import type { Reservation, ReservationRequest } from '../types/reservation'

const api = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/reservas`,
})

/** Extrae el mensaje de error del backend (`{ status, message }`) o usa uno genérico. */
export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    return error.response?.data?.message ?? 'No se pudo conectar con el servidor'
  }
  return 'Ocurrió un error inesperado'
}

/** Cliente HTTP de la API REST de reservas. */
export const ReservationService = {
  /** Obtiene todas las reservas (activas y canceladas). */
  async getAll(): Promise<Reservation[]> {
    const { data } = await api.get<Reservation[]>('')
    return data
  },

  /** Crea una reserva; falla con el mensaje del backend si la franja está ocupada. */
  async create(request: ReservationRequest): Promise<Reservation> {
    const { data } = await api.post<Reservation>('', request)
    return data
  },

  /** Cancela la reserva con el id indicado. */
  async cancel(id: number): Promise<void> {
    await api.delete(`/${id}`)
  },
}
