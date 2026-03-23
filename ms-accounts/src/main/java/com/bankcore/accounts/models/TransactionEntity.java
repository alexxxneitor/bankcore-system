package com.bankcore.accounts.models;

import com.bankcore.accounts.utils.enums.TransactionStatus;
import com.bankcore.accounts.utils.enums.TransactionType;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a financial transaction associated with an {@link AccountEntity}.
 * <p>
 * This entity is immutable and mapped to the {@code transactions} table.
 * Each transaction is uniquely identified by a generated {@link UUID} and
 * a derived {@code referenceNumber}. The entity captures essential details
 * such as type, amount, balance after the transaction, concept, counterparty
 * information, and status.
 * </p>
 *
 * <p><b>Entidad de transacción financiera</b></p>
 * <p>
 * Esta entidad es inmutable y está mapeada a la tabla {@code transactions}.
 * Cada transacción se identifica de manera única mediante un {@link UUID}
 * generado y un {@code referenceNumber} derivado. La entidad almacena
 * información esencial como tipo, monto, balance posterior, concepto,
 * datos de la contraparte y estado.
 * </p>
 *
 * <p><b>Constraints and indexes / Restricciones e índices:</b></p>
 * <ul>
 *   <li>Unique constraint on {@code referenceNumber} / Restricción única en {@code referenceNumber}.</li>
 *   <li>Index on {@code account_id} for efficient lookups by account / Índice en {@code account_id} para búsquedas eficientes por cuenta.</li>
 *   <li>Index on {@code referenceNumber} for quick retrieval by reference / Índice en {@code referenceNumber} para recuperación rápida por referencia.</li>
 *   <li>Composite index on {@code account_id, createdAt DESC} for chronological queries / Índice compuesto en {@code account_id, createdAt DESC} para consultas cronológicas.</li>
 * </ul>
 *
 * <p><b>Lifecycle / Ciclo de vida:</b></p>
 * <ul>
 *   <li>On persist, {@code createdAt} is set to the current timestamp / Al persistir, {@code createdAt} se asigna con la marca de tiempo actual.</li>
 *   <li>A {@code referenceNumber} is generated based on the transaction {@link UUID} / Se genera un {@code referenceNumber} basado en el {@link UUID} de la transacción.</li>
 * </ul>
 *
 * @author Bankcore Team - Sebastian Orjuela
 * @version 1.0.1
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Immutable
@Entity
@Table(
        name = "transactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reference_number",
                        columnNames = "referenceNumber"
                )
        },
        indexes = {
                @Index(name = "idx_transaction_account", columnList = "account_id"),
                @Index(name = "idx_transaction_account_created", columnList = "account_id, created_at DESC"),
                @Index(name = "idx_account_type_created_at", columnList = "account_id, type, created_at DESC")
        }
)
public class TransactionEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private AccountEntity account;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal balanceAfter;

    @Column(updatable = false)
    private String description;

    @Column(updatable = false)
    private String counterpartyAccountNumber;

    @Column(updatable = false)
    private String counterpartyName;

    @Column(nullable = false, updatable = false)
    private String referenceNumber;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    /**
     * Generates and assigns a unique reference number for the transaction.
     * <p>
     * The reference number format:
     * </p>
     * <ul>
     *   <li>Prefix {@code TXN}.</li>
     *   <li>First 16 characters of the transaction {@link UUID}, uppercase.</li>
     *   <li>Random hexadecimal suffix from {@link ThreadLocalRandom}, uppercase.</li>
     * </ul>
     * <p>
     * Example: {@code TXN3F2504E04F89D41C7A9B3C2D}
     * </p>
     *
     * <p><b>Generación de referencia única:</b></p>
     * <ul>
     *   <li>Prefijo {@code TXN}.</li>
     *   <li>Primeros 16 caracteres del {@link UUID} de la transacción, en mayúsculas.</li>
     *   <li>Sufijo aleatorio en hexadecimal generado con {@link ThreadLocalRandom}, en mayúsculas.</li>
     * </ul>
     *
     * @implNote The combination of UUID v7 and random hex makes collisions
     *           extremely unlikely. Database constraints enforce uniqueness.
     * @implNote La combinación de UUID v7 y sufijo aleatorio hace que las colisiones
     *           sean altamente improbables. La restricción única en base de datos garantiza la unicidad.
     */
    protected void generateReferenceNumber() {
        if (this.id != null) {
            String uuidPart = this.id.toString()
                    .replace("-", "")
                    .substring(0, 16)
                    .toUpperCase();
            String randomPart = Long.toHexString(ThreadLocalRandom.current().nextLong(0xFFFFFFFFFFFFL)).toUpperCase();
            this.referenceNumber = String.join("","TXN", uuidPart, randomPart);
        }
    }

    /**
     * Lifecycle hook executed before persisting the entity.
     * <p>
     * Ensures that:
     * </p>
     * <ul>
     *   <li>{@code id} is initialized with a time-ordered UUID v7.</li>
     *   <li>{@code createdAt} is set to the current timestamp.</li>
     *   <li>{@code referenceNumber} is generated if not already present.</li>
     * </ul>
     *
     * <p><b>Gancho de ciclo de vida antes de persistir:</b></p>
     * <ul>
     *   <li>{@code id} se inicializa con un UUID v7 ordenado por tiempo.</li>
     *   <li>{@code createdAt} se asigna con la marca de tiempo actual.</li>
     *   <li>{@code referenceNumber} se genera si aún no existe.</li>
     * </ul>
     */
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch(); // UUID v7
        }

        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }

        if (this.referenceNumber == null) {
            generateReferenceNumber();
        }
    }
}
