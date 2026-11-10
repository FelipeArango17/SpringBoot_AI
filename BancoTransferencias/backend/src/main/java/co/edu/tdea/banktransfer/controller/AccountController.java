package co.edu.tdea.banktransfer.controller;

import co.edu.tdea.banktransfer.dto.AccountRequest;
import co.edu.tdea.banktransfer.dto.AccountResponse;
import co.edu.tdea.banktransfer.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints REST de las cuentas.
 */
@RestController
@RequestMapping("/api/cuentas")
@Tag(name = "Cuentas", description = "Consulta y apertura de cuentas")
public class AccountController {

    private final AccountService service;

    /**
     * @param service servicio de cuentas
     */
    public AccountController(AccountService service) {
        this.service = service;
    }

    /**
     * Lista las cuentas con su saldo.
     *
     * @return 200 con las cuentas
     */
    @GetMapping
    @Operation(summary = "Listar cuentas")
    public List<AccountResponse> findAll() {
        return service.findAll();
    }

    /**
     * Consulta una cuenta.
     *
     * @param accountNumber número de cuenta
     * @return 200 con la cuenta, o 404 si no existe
     */
    @GetMapping("/{accountNumber}")
    @Operation(summary = "Consultar una cuenta")
    public AccountResponse findByNumber(@PathVariable String accountNumber) {
        return service.findByNumber(accountNumber);
    }

    /**
     * Abre una cuenta nueva.
     *
     * @param request datos de la cuenta
     * @return 201 con la cuenta, 400 si es inválida o 409 si el número ya existe
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Abrir una cuenta")
    public AccountResponse create(@Valid @RequestBody AccountRequest request) {
        return service.create(request);
    }
}
