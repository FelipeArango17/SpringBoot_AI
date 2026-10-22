package co.edu.tdea.reservation.exception;

/**
 * Se lanza cuando se viola una regla de negocio (por ejemplo, la franja ya está ocupada).
 */
public class BusinessException extends RuntimeException {

    /**
     * @param message descripción de la regla violada
     */
    public BusinessException(String message) {
        super(message);
    }
}
