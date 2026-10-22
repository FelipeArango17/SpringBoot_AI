package co.edu.tdea.reservation.repository;

import co.edu.tdea.reservation.entity.Reservation;
import co.edu.tdea.reservation.entity.ReservationStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Repositorio Spring Data para la entidad {@link Reservation}.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Verifica si ya existe una reserva con el estado dado en una fecha y hora.
     * Spring Data genera la consulta a partir del nombre del método.
     *
     * @param date   fecha de la reserva
     * @param time   hora de la reserva
     * @param status estado a buscar (normalmente {@link ReservationStatus#ACTIVE})
     * @return {@code true} si existe una reserva que cumpla las condiciones
     */
    boolean existsByDateAndTimeAndStatus(LocalDate date, LocalTime time, ReservationStatus status);
}
