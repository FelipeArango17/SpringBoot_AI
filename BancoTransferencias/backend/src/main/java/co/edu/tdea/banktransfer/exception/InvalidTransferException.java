package co.edu.tdea.banktransfer.exception;

import org.springframework.http.HttpStatus;

/**
 * La transferencia es inválida: misma cuenta, monto no positivo o referencia desconocida (400).
 */
public final class InvalidTransferException extends BusinessException {

    /**
     * @param message mensaje en español para el usuario
     */
    public InvalidTransferException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
