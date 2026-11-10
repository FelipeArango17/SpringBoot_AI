import { useState, type FormEvent } from 'react'
import './AccountsPanel.css'
import { BankService, formatCop, getErrorMessage } from '../services/BankService'
import type { Account } from '../types/bank'

interface Props {
  accounts: Account[]
  onChanged: () => void
  onMessage: (toast: { message: string; type: 'error' | 'success' }) => void
}

/** Lista las cuentas con su saldo y permite abrir una cuenta nueva. */
export function AccountsPanel({ accounts, onChanged, onMessage }: Props) {
  const [open, setOpen] = useState(false)
  const [number, setNumber] = useState('')
  const [owner, setOwner] = useState('')
  const [balance, setBalance] = useState('')

  async function submit(event: FormEvent) {
    event.preventDefault()
    try {
      await BankService.createAccount({ accountNumber: number, ownerName: owner, initialBalance: Number(balance) })
      onMessage({ message: `Cuenta ${number} creada`, type: 'success' })
      setNumber(''); setOwner(''); setBalance(''); setOpen(false)
      onChanged()
    } catch (error) {
      onMessage({ message: getErrorMessage(error), type: 'error' })
    }
  }

  return (
    <section className="card">
      <div className="panel-head">
        <h2>Cuentas</h2>
        <button type="button" className="btn btn-light" onClick={() => setOpen(!open)}>
          {open ? 'Cancelar' : '+ Nueva cuenta'}
        </button>
      </div>
      {open && (
        <form className="new-account" onSubmit={submit}>
          <input placeholder="Número (6-20 dígitos)" value={number} onChange={(e) => setNumber(e.target.value)} required />
          <input placeholder="Titular" value={owner} onChange={(e) => setOwner(e.target.value)} required />
          <input type="number" min="0" step="0.01" placeholder="Saldo inicial" value={balance} onChange={(e) => setBalance(e.target.value)} required />
          <button type="submit" className="btn">Crear</button>
        </form>
      )}
      <ul className="accounts">
        {accounts.map((a) => (
          <li key={a.accountNumber}>
            <div>
              <strong>{a.ownerName}</strong>
              <span>{a.accountNumber}</span>
            </div>
            <b>{formatCop(a.balance)}</b>
          </li>
        ))}
        {accounts.length === 0 && <li className="empty">No hay cuentas registradas.</li>}
      </ul>
    </section>
  )
}
