package co.edu.tdea.banktransfer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro histórico de un intento de transferencia (completado o rechazado).
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private String reference;

    @Column(nullable = false, length = 20)
    private String sourceAccount;

    @Column(nullable = false, length = 20)
    private String targetAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private TransactionStatus status;

    private String failureReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Constructor requerido por JPA. */
    protected Transaction() {
    }

    /**
     * Crea un registro de transacción.
     *
     * @param sourceAccount cuenta origen
     * @param targetAccount cuenta destino
     * @param amount        monto
     * @param description   descripción opcional
     * @param status        resultado
     * @param failureReason motivo del rechazo (solo si fue rechazada)
     */
    public Transaction(String sourceAccount, String targetAccount, BigDecimal amount, String description,
                       TransactionStatus status, String failureReason) {
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.failureReason = failureReason;
    }

    @PrePersist
    void onCreate() {
        this.reference = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    /** @return identificador interno */
    public Long getId() { return id; }

    /** @return referencia única (UUID) */
    public String getReference() { return reference; }

    /** @return cuenta origen */
    public String getSourceAccount() { return sourceAccount; }

    /** @return cuenta destino */
    public String getTargetAccount() { return targetAccount; }

    /** @return monto */
    public BigDecimal getAmount() { return amount; }

    /** @return descripción */
    public String getDescription() { return description; }

    /** @return resultado */
    public TransactionStatus getStatus() { return status; }

    /** @return motivo del rechazo, o null */
    public String getFailureReason() { return failureReason; }

    /** @return fecha y hora */
    public LocalDateTime getCreatedAt() { return createdAt; }
}
