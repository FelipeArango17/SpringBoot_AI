package co.edu.tdea.banktransfer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Traduce las excepciones a respuestas {@link ProblemDetail} (RFC 9457).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Responde las violaciones de reglas de negocio con el código HTTP propio de cada una.
     *
     * @param ex excepción de negocio
     * @return problema con título, detalle y marca de tiempo
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusiness(BusinessException ex) {
        return build(ex.getStatus(), ex.getMessage());
    }

    /**
     * Responde los errores de validación de {@code @Valid} con 400 y los campos fallidos.
     *
     * @param ex excepción de validación
     * @return problema 400 con el detalle de cada campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining(" "));
        return build(HttpStatus.BAD_REQUEST, detail.isBlank() ? "Datos inválidos." : detail);
    }

    private ResponseEntity<ProblemDetail> build(HttpStatus status, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(problem);
    }
}
