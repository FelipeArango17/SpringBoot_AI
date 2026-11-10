/** Cuenta bancaria devuelta por la API. */
export interface Account {
  accountNumber: string
  ownerName: string
  balance: number
}

/** Estado de una transacción. */
export type TransactionStatus = 'COMPLETED' | 'REJECTED'

/** Transacción del histórico. */
export interface Transaction {
  reference: string
  sourceAccount: string
  targetAccount: string
  amount: number
  description: string | null
  status: TransactionStatus
  failureReason: string | null
  createdAt: string
}

/** Datos para transferir dinero. */
export interface TransferRequest {
  sourceAccount: string
  targetAccount: string
  amount: number
  description: string
}

/** Resultado de una transferencia: transacción + QR (data URI PNG) y su texto. */
export interface TransferResult {
  transaction: Transaction
  qrCode: string
  qrContent: string
}

/** Datos para abrir una cuenta. */
export interface AccountRequest {
  accountNumber: string
  ownerName: string
  initialBalance: number
}
