package co.edu.tdea.banktransfer.dto;

import co.edu.tdea.banktransfer.entity.Account;

import java.math.BigDecimal;

/**
 * Cuenta expuesta por la API.
 *
 * @param accountNumber número de cuenta
 * @param ownerName     titular
 * @param balance       saldo actual
 */
public record AccountResponse(String accountNumber, String ownerName, BigDecimal balance) {

    /**
     * Convierte una entidad en su DTO.
     *
     * @param account entidad
     * @return DTO equivalente
     */
    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getAccountNumber(), account.getOwnerName(), account.getBalance());
    }
}
