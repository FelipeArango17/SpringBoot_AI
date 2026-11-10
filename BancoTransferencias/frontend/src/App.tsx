import { useCallback, useEffect, useState } from 'react'
import './App.css'
import { AccountsPanel } from './components/AccountsPanel'
import { Toast } from './components/Toast'
import { TransactionHistory } from './components/TransactionHistory'
import { TransferForm } from './components/TransferForm'
import { QrModal } from './components/QrModal'
import { BankService, getErrorMessage } from './services/BankService'
import type { Account, Transaction, TransferResult } from './types/bank'

/** Pantalla principal: cuentas, formulario de transferencia e histórico. */
export default function App() {
  const [accounts, setAccounts] = useState<Account[]>([])
  const [history, setHistory] = useState<Transaction[]>([])
  const [filter, setFilter] = useState('')
  const [result, setResult] = useState<TransferResult | null>(null)
  const [qrOf, setQrOf] = useState<Transaction | null>(null)
  const [toast, setToast] = useState<{ message: string; type: 'error' | 'success' } | null>(null)

  const closeToast = useCallback(() => setToast(null), [])

  const reload = useCallback(async () => {
    try {
      const [acc, hist] = await Promise.all([BankService.getAccounts(), BankService.getHistory(filter || undefined)])
      setAccounts(acc)
      setHistory(hist)
    } catch (error) {
      setToast({ message: getErrorMessage(error), type: 'error' })
    }
  }, [filter])

  useEffect(() => { void reload() }, [reload])

  return (
    <>
      <header className="app-header">
        <h1>Banco TdeA</h1>
        <p>Transferencias entre cuentas · histórico · comprobante con código QR</p>
      </header>
      <main className="layout">
        <AccountsPanel accounts={accounts} onChanged={reload} onMessage={setToast} />
        <TransferForm
          accounts={accounts}
          onDone={(r) => { setResult(r); void reload() }}
          onError={(message) => { setToast({ message, type: 'error' }); void reload() }}
        />
        <div className="full">
          <TransactionHistory
            transactions={history}
            accounts={accounts}
            filter={filter}
            onFilter={setFilter}
            onShowQr={setQrOf}
          />
        </div>
      </main>
      {result && (
        <QrModal
          title="Transferencia exitosa"
          transaction={result.transaction}
          qrSrc={result.qrCode}
          qrContent={result.qrContent}
          onClose={() => setResult(null)}
        />
      )}
      {qrOf && (
        <QrModal
          title="Comprobante de la transacción"
          transaction={qrOf}
          qrSrc={BankService.qrUrl(qrOf.reference)}
          onClose={() => setQrOf(null)}
        />
      )}
      {toast && <Toast message={toast.message} type={toast.type} onClose={closeToast} />}
    </>
  )
}
