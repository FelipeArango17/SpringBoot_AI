package co.edu.tdea.reservation.service;

import co.edu.tdea.reservation.dto.ReservationRequest;
import co.edu.tdea.reservation.entity.Reservation;
import co.edu.tdea.reservation.entity.ReservationStatus;
import co.edu.tdea.reservation.exception.BusinessException;
import co.edu.tdea.reservation.exception.InvalidReservationException;
import co.edu.tdea.reservation.exception.ReservationNotFoundException;
import co.edu.tdea.reservation.repository.ReservationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio para la gestión de reservas.
 */
@Service
public class ReservationService {

    private final ReservationRepository repository;

    /**
     * @param repository acceso a datos de las reservas
     */
    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista todas las reservas, incluidas las canceladas.
     *
     * @return todas las reservas
     */
    public List<Reservation> findAll() {
        return repository.findAll();
    }

    /**
     * Crea una reserva si todos los campos están presentes y la franja de fecha y hora está libre.
     *
     * @param request datos de la reserva
     * @return reserva guardada
     * @throws InvalidReservationException si falta algún campo
     * @throws BusinessException            si ya existe una reserva activa en la misma fecha y hora
     */
    @Transactional
    public Reservation create(ReservationRequest request) {
        if (request == null || isBlank(request.customerName()) || isBlank(request.service())
                || request.date() == null || request.time() == null) {
            throw new InvalidReservationException("El nombre del cliente, la fecha, la hora y el servicio son obligatorios");
        }
        // Solo las reservas activas bloquean la franja; las canceladas la dejan libre
        if (repository.existsByDateAndTimeAndStatus(request.date(), request.time(), ReservationStatus.ACTIVE)) {
            throw new BusinessException("Ya existe una reserva para el " + request.date()
                    + " a las " + request.time());
        }
        return repository.save(new Reservation(request.customerName().trim(), request.date(),
                request.time(), request.service().trim()));
    }

    /**
     * Cancela una reserva (borrado lógico: la fila se conserva con estado CANCELLED).
     *
     * @param id identificador de la reserva
     * @throws ReservationNotFoundException si la reserva no existe
     * @throws BusinessException            si ya estaba cancelada
     */
    @Transactional
    public void cancel(Long id) {
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException("La reserva " + id + " ya está cancelada");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        repository.save(reservation);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
