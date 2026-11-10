package co.edu.tdea.banktransfer.repository;

import co.edu.tdea.banktransfer.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de las cuentas.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Busca una cuenta por su número.
     *
     * @param accountNumber número de cuenta
     * @return la cuenta, si existe
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Busca una cuenta bloqueando la fila (escritura pesimista) para mover dinero con seguridad.
     *
     * @param accountNumber número de cuenta
     * @return la cuenta bloqueada, si existe
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountNumber = :accountNumber")
    Optional<Account> findForUpdate(@Param("accountNumber") String accountNumber);

    /**
     * Indica si ya existe una cuenta con ese número.
     *
     * @param accountNumber número de cuenta
     * @return true si existe
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * Lista las cuentas ordenadas por número.
     *
     * @return cuentas ordenadas
     */
    List<Account> findAllByOrderByAccountNumberAsc();
}
