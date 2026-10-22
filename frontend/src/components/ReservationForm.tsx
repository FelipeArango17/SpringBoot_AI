import { useState } from 'react'
import type { SyntheticEvent } from 'react'
import { ReservationService, getErrorMessage } from '../services/ReservationService'
import './ReservationForm.css'

const SERVICES = ['Corte de cabello', 'Manicure', 'Masaje', 'Tratamiento facial', 'Consulta general']

const EMPTY = { customerName: '', date: '', time: '', service: '' }

interface ReservationFormProps {
  onCreated: () => void
  onError: (message: string) => void
}

/** Formulario para crear una reserva. Todos los campos son obligatorios; los errores del backend se reportan con `onError`. */
export function ReservationForm({ onCreated, onError }: ReservationFormProps) {
  const [form, setForm] = useState(EMPTY)
  const [submitting, setSubmitting] = useState(false)

  const update = (field: keyof typeof EMPTY) => (
    e: { target: { value: string } },
  ) => setForm({ ...form, [field]: e.target.value })

  const handleSubmit = async (e: SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault()
    setSubmitting(true)
    try {
      await ReservationService.create(form)
      setForm(EMPTY)
      onCreated()
    } catch (error) {
      onError(getErrorMessage(error))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form className="reservation-form" onSubmit={handleSubmit}>
      <label>
        Nombre del cliente
        <input type="text" value={form.customerName} onChange={update('customerName')} required />
      </label>
      <label>
        Fecha
        <input type="date" value={form.date} onChange={update('date')} required />
      </label>
      <label>
        Hora
        <input type="time" value={form.time} onChange={update('time')} required />
      </label>
      <label>
        Servicio
        <select value={form.service} onChange={update('service')} required>
          <option value="" disabled>Selecciona un servicio</option>
          {SERVICES.map((s) => (
            <option key={s} value={s}>{s}</option>
          ))}
        </select>
      </label>
      <button type="submit" disabled={submitting}>
        {submitting ? 'Guardando…' : 'Crear reserva'}
      </button>
    </form>
  )
}
