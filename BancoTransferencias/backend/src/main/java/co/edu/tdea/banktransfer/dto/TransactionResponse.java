package co.edu.tdea.banktransfer.dto;

import co.edu.tdea.banktransfer.entity.Transaction;
import co.edu.tdea.banktransfer.entity.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transacción expuesta por la API.
 *
 * @param reference     referencia única
 * @param sourceAccount cuenta origen
 * @param targetAccount cuenta destino
 * @param amount        monto
 * @param description   descripción
 * @param status        resultado
 * @param failureReason motivo del rechazo, o null
 * @param createdAt     fecha y hora
 */
public record TransactionResponse(String reference, String sourceAccount, String targetAccount,
                                  BigDecimal amount, String description, TransactionStatus status,
                                  String failureReason, LocalDateTime createdAt) {

    /**
     * Convierte una entidad en su DTO.
     *
     * @param t entidad
     * @return DTO equivalente
     */
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(t.getReference(), t.getSourceAccount(), t.getTargetAccount(),
                t.getAmount(), t.getDescription(), t.getStatus(), t.getFailureReason(), t.getCreatedAt());
    }
}
