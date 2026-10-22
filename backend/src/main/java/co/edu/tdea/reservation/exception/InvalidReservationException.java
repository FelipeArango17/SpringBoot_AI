package co.edu.tdea.reservation.exception;

/**
 * Se lanza cuando los datos de una reserva están incompletos o son inválidos.
 */
public class InvalidReservationException extends RuntimeException {

    /**
     * @param message descripción del problema con los datos
     */
    public InvalidReservationException(String message) {
        super(message);
    }
}
