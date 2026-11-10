import './TransactionHistory.css'
import { formatCop } from '../services/BankService'
import type { Account, Transaction } from '../types/bank'

interface Props {
  transactions: Transaction[]
  accounts: Account[]
  filter: string
  onFilter: (account: string) => void
  onShowQr: (transaction: Transaction) => void
}

/** Tabla con el histórico de transacciones (completadas y rechazadas) y filtro por cuenta. */
export function TransactionHistory({ transactions, accounts, filter, onFilter, onShowQr }: Props) {
  return (
    <section className="card">
      <div className="history-head">
        <h2>Histórico de transacciones</h2>
        <select value={filter} onChange={(e) => onFilter(e.target.value)} aria-label="Filtrar por cuenta">
          <option value="">Todas las cuentas</option>
          {accounts.map((a) => <option key={a.accountNumber} value={a.accountNumber}>{a.accountNumber} · {a.ownerName}</option>)}
        </select>
      </div>
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Fecha</th><th>Origen</th><th>Destino</th><th>Monto</th><th>Estado</th><th>Detalle</th><th>QR</th></tr>
          </thead>
          <tbody>
            {transactions.map((t) => (
              <tr key={t.reference}>
                <td>{new Date(t.createdAt).toLocaleString('es-CO')}</td>
                <td>{t.sourceAccount}</td>
                <td>{t.targetAccount}</td>
                <td className="num">{formatCop(t.amount)}</td>
                <td><span className={`badge badge-${t.status.toLowerCase()}`}>{t.status === 'COMPLETED' ? 'Completada' : 'Rechazada'}</span></td>
                <td>{t.failureReason ?? t.description ?? '—'}</td>
                <td>{t.status === 'COMPLETED'
                  ? <button type="button" className="btn btn-light small" onClick={() => onShowQr(t)}>Ver QR</button>
                  : '—'}</td>
              </tr>
            ))}
            {transactions.length === 0 && <tr><td colSpan={7} className="empty">Aún no hay transacciones.</td></tr>}
          </tbody>
        </table>
      </div>
    </section>
  )
}
