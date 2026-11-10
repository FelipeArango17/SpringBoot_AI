package co.edu.tdea.banktransfer.service;

import co.edu.tdea.banktransfer.dto.AccountRequest;
import co.edu.tdea.banktransfer.dto.AccountResponse;
import co.edu.tdea.banktransfer.entity.Account;
import co.edu.tdea.banktransfer.exception.AccountNotFoundException;
import co.edu.tdea.banktransfer.exception.DuplicateAccountException;
import co.edu.tdea.banktransfer.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio de las cuentas.
 */
@Service
public class AccountService {

    private final AccountRepository repository;

    /**
     * @param repository acceso a datos de cuentas
     */
    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista todas las cuentas.
     *
     * @return cuentas ordenadas por número
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return repository.findAllByOrderByAccountNumberAsc().stream().map(AccountResponse::from).toList();
    }

    /**
     * Consulta una cuenta por número.
     *
     * @param accountNumber número de cuenta
     * @return la cuenta
     * @throws AccountNotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public AccountResponse findByNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber).map(AccountResponse::from)
                .orElseThrow(() -> new AccountNotFoundException("La cuenta " + accountNumber + " no existe."));
    }

    /**
     * Abre una cuenta nueva.
     *
     * @param request datos de la cuenta
     * @return la cuenta creada
     * @throws DuplicateAccountException si el número ya existe
     */
    @Transactional
    public AccountResponse create(AccountRequest request) {
        String number = request.accountNumber().trim();
        if (repository.existsByAccountNumber(number)) {
            throw new DuplicateAccountException("Ya existe una cuenta con el número " + number + ".");
        }
        return AccountResponse.from(repository.save(
                new Account(number, request.ownerName().trim(), request.initialBalance())));
    }
}
