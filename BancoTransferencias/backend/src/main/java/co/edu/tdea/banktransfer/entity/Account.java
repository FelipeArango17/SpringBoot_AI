package co.edu.tdea.banktransfer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cuenta bancaria con su saldo.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    /** Constructor requerido por JPA. */
    protected Account() {
    }

    /**
     * Crea una cuenta.
     *
     * @param accountNumber número de cuenta
     * @param ownerName     titular
     * @param balance       saldo inicial
     */
    public Account(String accountNumber, String ownerName, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Descuenta dinero del saldo.
     *
     * @param amount monto a debitar (mayor que cero)
     * @throws IllegalStateException si el saldo no alcanza
     */
    public void debit(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Saldo insuficiente");
        }
        this.balance = balance.subtract(amount);
    }

    /**
     * Suma dinero al saldo.
     *
     * @param amount monto a acreditar
     */
    public void credit(BigDecimal amount) {
        this.balance = balance.add(amount);
    }

    /** @return identificador interno */
    public Long getId() { return id; }

    /** @return número de cuenta */
    public String getAccountNumber() { return accountNumber; }

    /** @return nombre del titular */
    public String getOwnerName() { return ownerName; }

    /** @return saldo actual */
    public BigDecimal getBalance() { return balance; }

    /** @return fecha de creación */
    public LocalDateTime getCreatedAt() { return createdAt; }
}
