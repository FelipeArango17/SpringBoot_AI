import { useCallback, useEffect, useState } from 'react'
import { ReservationService, getErrorMessage } from '../services/ReservationService'
import type { Reservation } from '../types/reservation'
import './ReservationTable.css'

interface ReservationTableProps {
  /** Al cambiar este valor se recarga la lista. */
  refreshKey: number
  onError: (message: string) => void
}

/** Tabla con todas las reservas y un botón para cancelar las activas. */
export function ReservationTable({ refreshKey, onError }: ReservationTableProps) {
  const [reservations, setReservations] = useState<Reservation[]>([])
  const [loading, setLoading] = useState(true)

  const load = useCallback(async () => {
    try {
      setReservations(await ReservationService.getAll())
    } catch (error) {
      onError(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [onError])

  useEffect(() => {
    load()
  }, [load, refreshKey])

  const handleCancel = async (id: number) => {
    try {
      await ReservationService.cancel(id)
      await load()
    } catch (error) {
      onError(getErrorMessage(error))
    }
  }

  if (loading) return <p className="table-empty">Cargando reservas…</p>
  if (reservations.length === 0) return <p className="table-empty">No hay reservas todavía.</p>

  return (
    <table className="reservation-table">
      <thead>
        <tr>
          <th>#</th>
          <th>Cliente</th>
          <th>Fecha</th>
          <th>Hora</th>
          <th>Servicio</th>
          <th>Estado</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {reservations.map((r) => (
          <tr key={r.id} className={r.status === 'CANCELLED' ? 'cancelled' : ''}>
            <td>{r.id}</td>
            <td>{r.customerName}</td>
            <td>{r.date}</td>
            <td>{r.time.slice(0, 5)}</td>
            <td>{r.service}</td>
            <td>
              <span className={`badge badge-${r.status.toLowerCase()}`}>
                {r.status === 'ACTIVE' ? 'Activa' : 'Cancelada'}
              </span>
            </td>
            <td>
              {r.status === 'ACTIVE' && (
                <button type="button" className="btn-cancel" onClick={() => handleCancel(r.id)}>
                  Cancelar
                </button>
              )}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}
