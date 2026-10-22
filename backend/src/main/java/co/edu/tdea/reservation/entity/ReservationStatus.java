package co.edu.tdea.reservation.entity;

/**
 * Estados posibles de una {@link Reservation}.
 */
public enum ReservationStatus {
    /** La reserva está vigente y ocupa su franja de fecha y hora. */
    ACTIVE,
    /** La reserva fue cancelada y su franja vuelve a estar libre. */
    CANCELLED
}
