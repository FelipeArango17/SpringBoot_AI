package co.edu.tdea.banktransfer.controller;

import co.edu.tdea.banktransfer.dto.TransactionResponse;
import co.edu.tdea.banktransfer.dto.TransferRequest;
import co.edu.tdea.banktransfer.dto.TransferResponse;
import co.edu.tdea.banktransfer.service.QrCodeService;
import co.edu.tdea.banktransfer.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints REST de transferencias, histórico y QR.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Transferencias", description = "Transferir dinero, consultar el histórico y descargar el QR")
public class TransferController {

    private final TransferService transferService;
    private final QrCodeService qrCodeService;

    /**
     * @param transferService servicio de transferencias
     * @param qrCodeService   generador de QR
     */
    public TransferController(TransferService transferService, QrCodeService qrCodeService) {
        this.transferService = transferService;
        this.qrCodeService = qrCodeService;
    }

    /**
     * Realiza una transferencia y devuelve el comprobante con su QR.
     *
     * @param request datos de la transferencia
     * @return 201 con la transacción y el QR; 400, 404 o 422 si se viola una regla
     */
    @PostMapping("/transferencias")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Transferir dinero entre cuentas")
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transferService.transfer(request);
    }

    /**
     * Consulta el histórico de transacciones.
     *
     * @param account número de cuenta para filtrar (opcional)
     * @return 200 con las transacciones, de la más reciente a la más antigua
     */
    @GetMapping("/transacciones")
    @Operation(summary = "Histórico de transacciones")
    public List<TransactionResponse> history(@RequestParam(name = "cuenta", required = false) String account) {
        return transferService.findHistory(account);
    }

    /**
     * Descarga el QR de una transacción como imagen PNG.
     *
     * @param reference referencia de la transacción
     * @return 200 con el PNG, o 400 si la referencia no existe
     */
    @GetMapping(value = "/transacciones/{reference}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "QR de una transacción (PNG)")
    public ResponseEntity<byte[]> qr(@PathVariable String reference) {
        TransactionResponse transaction = transferService.findByReference(reference);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCodeService.generatePng(transaction));
    }
}
