package co.edu.tdea.reservation.dto;

/**
 * Cuerpo de error que devuelve la API cuando una petición falla.
 *
 * @param status  código de estado HTTP
 * @param message descripción del problema legible por una persona
 */
public record ErrorResponse(int status, String message) {
}
