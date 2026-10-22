package co.edu.tdea.reservation.exception;

import co.edu.tdea.reservation.dto.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las excepciones del dominio a respuestas de error HTTP.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Las violaciones de reglas de negocio se responden con 409 Conflict.
     *
     * @param ex excepción capturada
     * @return respuesta de error
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Los datos incompletos o inválidos se responden con 400 Bad Request.
     *
     * @param ex excepción capturada
     * @return respuesta de error
     */
    @ExceptionHandler(InvalidReservationException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(InvalidReservationException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Las reservas inexistentes se responden con 404 Not Found.
     *
     * @param ex excepción capturada
     * @return respuesta de error
     */
    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ReservationNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), message));
    }
}
