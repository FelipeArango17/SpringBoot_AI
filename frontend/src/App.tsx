import { useCallback, useState } from 'react'
import { ReservationForm } from './components/ReservationForm'
import { ReservationTable } from './components/ReservationTable'
import { Toast } from './components/Toast'
import './App.css'

interface ToastState {
  message: string
  type: 'error' | 'success'
}

/** Página principal: formulario de creación arriba y tabla de reservas abajo. */
function App() {
  const [refreshKey, setRefreshKey] = useState(0)
  const [toast, setToast] = useState<ToastState | null>(null)

  const showError = useCallback((message: string) => setToast({ message, type: 'error' }), [])
  const closeToast = useCallback(() => setToast(null), [])

  const handleCreated = () => {
    setRefreshKey((k) => k + 1)
    setToast({ message: 'Reserva creada correctamente', type: 'success' })
  }

  return (
    <main className="app">
      <h1>Sistema de reservas</h1>
      <section className="card">
        <h2>Nueva reserva</h2>
        <ReservationForm onCreated={handleCreated} onError={showError} />
      </section>
      <section className="card">
        <h2>Reservas</h2>
        <ReservationTable refreshKey={refreshKey} onError={showError} />
      </section>
      {toast && <Toast message={toast.message} type={toast.type} onClose={closeToast} />}
    </main>
  )
}

export default App
