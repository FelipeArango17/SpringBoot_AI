import axios from 'axios'
import type { Account, AccountRequest, Transaction, TransferRequest, TransferResult } from '../types/bank'

const baseURL = `${import.meta.env.VITE_API_URL}/api`
const api = axios.create({ baseURL })

/** Extrae el mensaje del ProblemDetail del backend (`detail`) o usa uno genérico. */
export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    return error.response?.data?.detail ?? 'No se pudo conectar con el servidor'
  }
  return 'Ocurrió un error inesperado'
}

/** Formatea un monto en pesos colombianos. */
export function formatCop(value: number): string {
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 2 }).format(value)
}

/** Cliente HTTP de la API del banco (única capa que llama al backend). */
export const BankService = {
  /** Lista todas las cuentas con su saldo. */
  async getAccounts(): Promise<Account[]> {
    const { data } = await api.get<Account[]>('/cuentas')
    return data
  },

  /** Abre una cuenta nueva. */
  async createAccount(request: AccountRequest): Promise<Account> {
    const { data } = await api.post<Account>('/cuentas', request)
    return data
  },

  /** Transfiere dinero; devuelve la transacción y su QR. Falla con el motivo del backend. */
  async transfer(request: TransferRequest): Promise<TransferResult> {
    const { data } = await api.post<TransferResult>('/transferencias', request)
    return data
  },

  /** Histórico de transacciones, con filtro opcional por número de cuenta. */
  async getHistory(account?: string): Promise<Transaction[]> {
    const { data } = await api.get<Transaction[]>('/transacciones', { params: account ? { cuenta: account } : {} })
    return data
  },

  /** URL de la imagen PNG del QR de una transacción. */
  qrUrl(reference: string): string {
    return `${baseURL}/transacciones/${reference}/qr`
  },
}
