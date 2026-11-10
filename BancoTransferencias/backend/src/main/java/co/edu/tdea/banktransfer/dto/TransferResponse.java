package co.edu.tdea.banktransfer.dto;

/**
 * Resultado de una transferencia: la transacción y su código QR.
 *
 * @param transaction transacción registrada
 * @param qrCode      imagen PNG del QR como data URI en base64
 * @param qrContent   texto codificado dentro del QR
 */
public record TransferResponse(TransactionResponse transaction, String qrCode, String qrContent) {
}
