package co.edu.tdea.reservation.dto;

import co.edu.tdea.reservation.entity.Reservation;
import co.edu.tdea.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Datos de una reserva que la API devuelve al cliente.
 *
 * @param id           identificador de la reserva
 * @param customerName nombre del cliente
 * @param date         fecha de la reserva
 * @param time         hora de la reserva
 * @param service      servicio solicitado
 * @param status       estado actual
 */
public record ReservationResponse(Long id, String customerName, LocalDate date, LocalTime time,
                                  String service, ReservationStatus status) {

    /**
     * Convierte una entidad en su representación de respuesta.
     *
     * @param reservation entidad persistida
     * @return DTO equivalente
     */
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation.getId(), reservation.getCustomerName(),
                reservation.getDate(), reservation.getTime(), reservation.getService(),
                reservation.getStatus());
    }
}
