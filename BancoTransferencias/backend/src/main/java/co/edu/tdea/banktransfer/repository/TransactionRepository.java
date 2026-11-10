package co.edu.tdea.banktransfer.repository;

import co.edu.tdea.banktransfer.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos del histórico de transacciones.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Lista todo el histórico, de la más reciente a la más antigua.
     *
     * @return transacciones ordenadas
     */
    List<Transaction> findAllByOrderByCreatedAtDescIdDesc();

    /**
     * Lista las transacciones donde la cuenta es origen o destino.
     *
     * @param source cuenta como origen
     * @param target cuenta como destino (mismo valor)
     * @return transacciones ordenadas de la más reciente a la más antigua
     */
    List<Transaction> findBySourceAccountOrTargetAccountOrderByCreatedAtDescIdDesc(String source, String target);

    /**
     * Busca una transacción por su referencia.
     *
     * @param reference referencia única
     * @return la transacción, si existe
     */
    Optional<Transaction> findByReference(String reference);
}
