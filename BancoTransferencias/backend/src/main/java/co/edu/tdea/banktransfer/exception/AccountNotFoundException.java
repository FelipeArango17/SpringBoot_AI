package co.edu.tdea.banktransfer.exception;

import org.springframework.http.HttpStatus;

/**
 * La cuenta indicada no existe (404).
 */
public final class AccountNotFoundException extends BusinessException {

    /**
     * @param message mensaje en español para el usuario
     */
    public AccountNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
