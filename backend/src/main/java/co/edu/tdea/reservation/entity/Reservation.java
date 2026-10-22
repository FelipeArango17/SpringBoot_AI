package co.edu.tdea.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad JPA que representa la reserva de un servicio por parte de un cliente
 * en una fecha y hora determinadas. Se guarda en la tabla {@code reservations}.
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    // Se usan nombres de columna propios para evitar choques con palabras reservadas de SQL
    @Column(name = "reservation_date", nullable = false)
    private LocalDate date;

    @Column(name = "reservation_time", nullable = false)
    private LocalTime time;

    @Column(nullable = false)
    private String service;

    // Se guarda como texto (ACTIVE/CANCELLED) para que no dependa del orden del enum
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    /** Constructor vacío exigido por JPA. */
    public Reservation() {
    }

    /**
     * Crea una reserva en estado {@link ReservationStatus#ACTIVE}.
     *
     * @param customerName nombre del cliente
     * @param date         fecha de la reserva
     * @param time         hora de la reserva
     * @param service      servicio solicitado
     */
    public Reservation(String customerName, LocalDate date, LocalTime time, String service) {
        this.customerName = customerName;
        this.date = date;
        this.time = time;
        this.service = service;
        this.status = ReservationStatus.ACTIVE;
    }

    /** @return identificador generado automáticamente */
    public Long getId() {
        return id;
    }

    /** @param id identificador de la reserva */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return nombre del cliente */
    public String getCustomerName() {
        return customerName;
    }

    /** @param customerName nombre del cliente */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /** @return fecha de la reserva */
    public LocalDate getDate() {
        return date;
    }

    /** @param date fecha de la reserva */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /** @return hora de la reserva */
    public LocalTime getTime() {
        return time;
    }

    /** @param time hora de la reserva */
    public void setTime(LocalTime time) {
        this.time = time;
    }

    /** @return servicio solicitado */
    public String getService() {
        return service;
    }

    /** @param service servicio solicitado */
    public void setService(String service) {
        this.service = service;
    }

    /** @return estado actual de la reserva */
    public ReservationStatus getStatus() {
        return status;
    }

    /** @param status nuevo estado de la reserva */
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
