package co.edu.tdea.banktransfer.entity;

/**
 * Resultado de un intento de transferencia.
 */
public enum TransactionStatus {
    /** La transferencia se ejecutó y movió el dinero. */
    COMPLETED,
    /** La transferencia violó una regla de negocio y no movió dinero. */
    REJECTED
}
