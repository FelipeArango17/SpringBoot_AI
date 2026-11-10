package co.edu.tdea.banktransfer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Datos de una transferencia.
 *
 * @param sourceAccount cuenta origen
 * @param targetAccount cuenta destino
 * @param amount        monto a transferir (mayor que cero)
 * @param description   descripción opcional
 */
public record TransferRequest(
        @NotBlank(message = "La cuenta origen es obligatoria.") String sourceAccount,
        @NotBlank(message = "La cuenta destino es obligatoria.") String targetAccount,
        @NotNull(message = "El monto es obligatorio.")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero.") BigDecimal amount,
        @Size(max = 120, message = "La descripción admite máximo 120 caracteres.") String description) {
}
