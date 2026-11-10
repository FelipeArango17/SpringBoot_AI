package co.edu.tdea.banktransfer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

/**
 * Datos para abrir una cuenta.
 *
 * @param accountNumber  número de cuenta (6 a 20 dígitos)
 * @param ownerName      nombre del titular
 * @param initialBalance saldo inicial (cero o más)
 */
public record AccountRequest(
        @NotBlank(message = "El número de cuenta es obligatorio.")
        @Pattern(regexp = "\\d{6,20}", message = "El número de cuenta debe tener entre 6 y 20 dígitos.")
        String accountNumber,
        @NotBlank(message = "El nombre del titular es obligatorio.")
        String ownerName,
        @NotNull(message = "El saldo inicial es obligatorio.")
        @DecimalMin(value = "0.00", message = "El saldo inicial no puede ser negativo.")
        BigDecimal initialBalance) {
}
