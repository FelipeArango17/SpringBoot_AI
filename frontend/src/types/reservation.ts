/** Estado de una reserva, tal como lo devuelve el backend. */
export type ReservationStatus = 'ACTIVE' | 'CANCELLED'

/** Reserva tal como la devuelve `GET /reservas`. */
export interface Reservation {
  id: number
  customerName: string
  date: string
  time: string
  service: string
  status: ReservationStatus
}

/** Datos que se envían a `POST /reservas`. */
export interface ReservationRequest {
  customerName: string
  date: string
  time: string
  service: string
}
