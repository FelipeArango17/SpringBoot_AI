package co.edu.tdea.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Datos de entrada para crear una reserva.
 *
 * @param customerName nombre del cliente
 * @param date         fecha de la reserva en formato ISO (ej. 2026-11-05)
 * @param time         hora de la reserva en formato ISO (ej. 14:30)
 * @param service      servicio solicitado
 */
public record ReservationRequest(String customerName, LocalDate date, LocalTime time, String service) {
}
