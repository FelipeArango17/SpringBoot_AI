import { useState, type FormEvent } from 'react'
import './TransferForm.css'
import { BankService, formatCop, getErrorMessage } from '../services/BankService'
import type { Account, TransferResult } from '../types/bank'

interface Props {
  accounts: Account[]
  onDone: (result: TransferResult) => void
  onError: (message: string) => void
}

/** Formulario controlado para transferir dinero entre dos cuentas. */
export function TransferForm({ accounts, onDone, onError }: Props) {
  const [source, setSource] = useState('')
  const [target, setTarget] = useState('')
  const [amount, setAmount] = useState('')
  const [description, setDescription] = useState('')
  const [sending, setSending] = useState(false)

  const sourceAccount = accounts.find((a) => a.accountNumber === source)
  const sameAccount = source !== '' && source === target
  const valid = source !== '' && target !== '' && !sameAccount && Number(amount) > 0

  async function submit(event: FormEvent) {
    event.preventDefault()
    if (!valid) return
    setSending(true)
    try {
      const result = await BankService.transfer({ sourceAccount: source, targetAccount: target, amount: Number(amount), description })
      onDone(result)
      setAmount(''); setDescription('')
    } catch (error) {
      onError(getErrorMessage(error))
    } finally {
      setSending(false)
    }
  }

  return (
    <section className="card">
      <h2>Transferir dinero</h2>
      <form onSubmit={submit}>
        <div className="field">
          <label htmlFor="source">Cuenta origen</label>
          <select id="source" value={source} onChange={(e) => setSource(e.target.value)} required>
            <option value="">Selecciona…</option>
            {accounts.map((a) => <option key={a.accountNumber} value={a.accountNumber}>{a.accountNumber} · {a.ownerName}</option>)}
          </select>
          {sourceAccount && <small className="hint">Saldo disponible: {formatCop(sourceAccount.balance)}</small>}
        </div>
        <div className="field">
          <label htmlFor="target">Cuenta destino</label>
          <select id="target" value={target} onChange={(e) => setTarget(e.target.value)} required>
            <option value="">Selecciona…</option>
            {accounts.map((a) => <option key={a.accountNumber} value={a.accountNumber}>{a.accountNumber} · {a.ownerName}</option>)}
          </select>
          {sameAccount && <div className="error">La cuenta origen y destino deben ser distintas.</div>}
        </div>
        <div className="field">
          <label htmlFor="amount">Monto (COP)</label>
          <input id="amount" type="number" min="0.01" step="0.01" value={amount} onChange={(e) => setAmount(e.target.value)} required />
        </div>
        <div className="field">
          <label htmlFor="description">Descripción (opcional)</label>
          <input id="description" maxLength={120} value={description} onChange={(e) => setDescription(e.target.value)} />
        </div>
        <button type="submit" className="btn" disabled={!valid || sending}>{sending ? 'Enviando…' : 'Transferir'}</button>
      </form>
    </section>
  )
}
