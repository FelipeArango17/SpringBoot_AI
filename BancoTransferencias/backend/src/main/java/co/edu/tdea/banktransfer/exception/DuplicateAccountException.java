package co.edu.tdea.banktransfer.exception;

import org.springframework.http.HttpStatus;

/**
 * Ya existe una cuenta con ese número (409).
 */
public final class DuplicateAccountException extends BusinessException {

    /**
     * @param message mensaje en español para el usuario
     */
    public DuplicateAccountException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
