package co.edu.tdea.reservation.exception;

/**
 * Se lanza cuando la reserva buscada no existe.
 */
public class ReservationNotFoundException extends RuntimeException {

    /**
     * @param id identificador que no se encontró
     */
    public ReservationNotFoundException(Long id) {
        super("No se encontró la reserva con id " + id);
    }
}
