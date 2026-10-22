import { useEffect } from 'react'
import './Toast.css'

interface ToastProps {
  message: string
  type: 'error' | 'success'
  onClose: () => void
}

/** Notificación flotante que se cierra sola a los 4 segundos. */
export function Toast({ message, type, onClose }: ToastProps) {
  useEffect(() => {
    const timer = setTimeout(onClose, 4000)
    return () => clearTimeout(timer)
  }, [message, onClose])

  return (
    <div className={`toast toast-${type}`} role="alert">
      <span>{message}</span>
      <button type="button" onClick={onClose} aria-label="Cerrar">×</button>
    </div>
  )
}
