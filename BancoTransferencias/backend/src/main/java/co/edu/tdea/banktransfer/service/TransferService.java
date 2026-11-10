package co.edu.tdea.banktransfer.service;

import co.edu.tdea.banktransfer.dto.TransactionResponse;
import co.edu.tdea.banktransfer.dto.TransferRequest;
import co.edu.tdea.banktransfer.dto.TransferResponse;
import co.edu.tdea.banktransfer.entity.Account;
import co.edu.tdea.banktransfer.entity.Transaction;
import co.edu.tdea.banktransfer.entity.TransactionStatus;
import co.edu.tdea.banktransfer.exception.AccountNotFoundException;
import co.edu.tdea.banktransfer.exception.BusinessException;
import co.edu.tdea.banktransfer.exception.InsufficientFundsException;
import co.edu.tdea.banktransfer.exception.InvalidTransferException;
import co.edu.tdea.banktransfer.repository.AccountRepository;
import co.edu.tdea.banktransfer.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * Lógica de negocio de las transferencias y del histórico.
 *
 * <p>El movimiento de dinero corre en una transacción de base de datos con bloqueo de filas. Si una
 * regla falla se hace rollback y el intento se guarda aparte como {@code REJECTED}, de modo que
 * también aparece en el histórico.</p>
 */
@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final QrCodeService qrCodeService;
    private final TransactionTemplate tx;
    private final TransactionTemplate newTx;

    /**
     * @param accountRepository     repositorio de cuentas
     * @param transactionRepository repositorio del histórico
     * @param qrCodeService         generador de QR
     * @param transactionManager    gestor de transacciones para el control programático
     */
    public TransferService(AccountRepository accountRepository, TransactionRepository transactionRepository,
                           QrCodeService qrCodeService, PlatformTransactionManager transactionManager) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.qrCodeService = qrCodeService;
        this.tx = new TransactionTemplate(transactionManager);
        this.newTx = new TransactionTemplate(transactionManager);
        this.newTx.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
    }

    /**
     * Transfiere dinero entre dos cuentas.
     *
     * @param request datos de la transferencia
     * @return la transacción completada y su QR
     * @throws BusinessException si una cuenta no existe, son la misma, el monto no es positivo o el saldo no alcanza
     */
    public TransferResponse transfer(TransferRequest request) {
        TransactionResponse completed;
        try {
            completed = tx.execute(status -> executeTransfer(request));
        } catch (BusinessException ex) {
            saveRejected(request, ex.getMessage());
            throw ex;
        }
        return new TransferResponse(completed, qrCodeService.generateDataUri(completed),
                qrCodeService.buildContent(completed));
    }

    /**
     * Consulta el histórico, con filtro opcional por cuenta.
     *
     * @param accountNumber cuenta a filtrar, o null/vacío para todas
     * @return transacciones de la más reciente a la más antigua
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> findHistory(String accountNumber) {
        List<Transaction> list = (accountNumber == null || accountNumber.isBlank())
                ? transactionRepository.findAllByOrderByCreatedAtDescIdDesc()
                : transactionRepository.findBySourceAccountOrTargetAccountOrderByCreatedAtDescIdDesc(
                        accountNumber.trim(), accountNumber.trim());
        return list.stream().map(TransactionResponse::from).toList();
    }

    /**
     * Busca una transacción por su referencia.
     *
     * @param reference referencia única
     * @return la transacción
     * @throws InvalidTransferException si no existe
     */
    @Transactional(readOnly = true)
    public TransactionResponse findByReference(String reference) {
        return transactionRepository.findByReference(reference).map(TransactionResponse::from)
                .orElseThrow(() -> new InvalidTransferException("No existe la transacción " + reference + "."));
    }

    private TransactionResponse executeTransfer(TransferRequest request) {
        String source = request.sourceAccount().trim();
        String target = request.targetAccount().trim();
        if (source.equals(target)) {
            throw new InvalidTransferException("La cuenta origen y la destino no pueden ser la misma.");
        }
        if (request.amount().signum() <= 0) {
            throw new InvalidTransferException("El monto debe ser mayor que cero.");
        }
        // Se bloquean siempre en el mismo orden para evitar deadlocks entre transferencias cruzadas
        boolean sourceFirst = source.compareTo(target) < 0;
        Account first = lock(sourceFirst ? source : target);
        Account second = lock(sourceFirst ? target : source);
        Account from = sourceFirst ? first : second;
        Account to = sourceFirst ? second : first;

        if (from.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException("Saldo insuficiente en la cuenta " + source + ".");
        }
        from.debit(request.amount());
        to.credit(request.amount());
        Transaction saved = transactionRepository.save(new Transaction(source, target, request.amount(),
                clean(request.description()), TransactionStatus.COMPLETED, null));
        return TransactionResponse.from(saved);
    }

    private Account lock(String number) {
        return accountRepository.findForUpdate(number)
                .orElseThrow(() -> new AccountNotFoundException("La cuenta " + number + " no existe."));
    }

    private void saveRejected(TransferRequest request, String reason) {
        newTx.executeWithoutResult(status -> transactionRepository.save(new Transaction(
                request.sourceAccount().trim(), request.targetAccount().trim(), request.amount(),
                clean(request.description()), TransactionStatus.REJECTED, reason)));
    }

    private String clean(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }
}
