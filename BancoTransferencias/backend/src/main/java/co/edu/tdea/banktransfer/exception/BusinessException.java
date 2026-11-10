package co.edu.tdea.banktransfer.exception;

import org.springframework.http.HttpStatus;

/**
 * Base de las violaciones de reglas de negocio; cada subtipo define su código HTTP.
 */
public abstract sealed class BusinessException extends RuntimeException
        permits AccountNotFoundException, DuplicateAccountException,
                InsufficientFundsException, InvalidTransferException {

    /**
     * @param message mensaje en español para el usuario
     */
    protected BusinessException(String message) {
        super(message);
    }

    /**
     * @return código HTTP con el que se responde esta violación
     */
    public abstract HttpStatus getStatus();
}
