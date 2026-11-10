package co.edu.tdea.banktransfer.exception;

import org.springframework.http.HttpStatus;

/**
 * La cuenta origen no tiene saldo suficiente (422).
 */
public final class InsufficientFundsException extends BusinessException {

    /**
     * @param message mensaje en español para el usuario
     */
    public InsufficientFundsException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }
}
