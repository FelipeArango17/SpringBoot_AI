package co.edu.tdea.banktransfer.config;

import co.edu.tdea.banktransfer.entity.Account;
import co.edu.tdea.banktransfer.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Inserta cuentas de ejemplo si la tabla está vacía. Se desactiva con {@code app.seed-data=false}.
 */
@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository repository;

    /**
     * @param repository repositorio de cuentas
     */
    public DataSeeder(AccountRepository repository) {
        this.repository = repository;
    }

    /**
     * Carga las cuentas de ejemplo.
     *
     * @param args argumentos (no se usan)
     */
    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new Account("1001001001", "Felipe Arango", new BigDecimal("2500000.00")),
                new Account("1001001002", "Laura Gómez", new BigDecimal("1200000.00")),
                new Account("1001001003", "Carlos Restrepo", new BigDecimal("850000.00")),
                new Account("1001001004", "Mariana Londoño", new BigDecimal("300000.00"))));
    }
}
