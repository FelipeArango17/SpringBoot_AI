import './QrModal.css'
import { formatCop } from '../services/BankService'
import type { Transaction } from '../types/bank'

interface Props {
  title: string
  transaction: Transaction
  qrSrc: string
  qrContent?: string
  onClose: () => void
}

/** Comprobante de una transferencia con su código QR descargable como PNG. */
export function QrModal({ title, transaction, qrSrc, qrContent, onClose }: Props) {
  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal card" role="dialog" aria-modal="true" onClick={(e) => e.stopPropagation()}>
        <h2>{title}</h2>
        <img src={qrSrc} alt="Código QR de la transacción" width={240} height={240} />
        <dl>
          <dt>Referencia</dt><dd className="mono">{transaction.reference}</dd>
          <dt>Origen → Destino</dt><dd>{transaction.sourceAccount} → {transaction.targetAccount}</dd>
          <dt>Monto</dt><dd>{formatCop(transaction.amount)}</dd>
          <dt>Fecha</dt><dd>{new Date(transaction.createdAt).toLocaleString('es-CO')}</dd>
        </dl>
        {qrContent && <pre className="qr-text">{qrContent}</pre>}
        <div className="actions">
          <a className="btn btn-light" href={qrSrc} download={`qr-${transaction.reference}.png`}>Descargar QR</a>
          <button type="button" className="btn" onClick={onClose}>Cerrar</button>
        </div>
      </div>
    </div>
  )
}
