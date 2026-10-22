package co.edu.tdea.reservation.controller;

import co.edu.tdea.reservation.dto.ReservationRequest;
import co.edu.tdea.reservation.dto.ReservationResponse;
import co.edu.tdea.reservation.service.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API REST de reservas. Documentación interactiva en {@code /swagger-ui/index.html}.
 */
@RestController
@RequestMapping("/reservas")
@Tag(name = "Reservations")
public class ReservationController {

    private final ReservationService service;

    /**
     * @param service lógica de negocio de las reservas
     */
    public ReservationController(ReservationService service) {
        this.service = service;
    }

    /**
     * Lista todas las reservas.
     *
     * @return 200 OK con las reservas
     */
    @GetMapping
    @Operation(summary = "List all reservations")
    public List<ReservationResponse> list() {
        return service.findAll().stream().map(ReservationResponse::from).toList();
    }

    /**
     * Crea una reserva.
     *
     * @param request datos de la reserva
     * @return 201 Created con la nueva reserva (400 si faltan datos, 409 si la franja está ocupada)
     */
    @PostMapping
    @Operation(summary = "Create a reservation")
    public ResponseEntity<ReservationResponse> create(@RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservationResponse.from(service.create(request)));
    }

    /**
     * Cancela una reserva.
     *
     * @param id identificador de la reserva
     * @return 204 No Content (404 si no existe, 409 si ya estaba cancelada)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a reservation")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        service.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
