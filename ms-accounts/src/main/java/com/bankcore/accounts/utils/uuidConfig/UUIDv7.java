package com.bankcore.accounts.utils.uuidConfig;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to mark entity fields or methods that should use
 * {@link UUIDv7Generator} for identifier generation.
 * <p>
 * This annotation integrates with Hibernate's {@link IdGeneratorType}
 * mechanism to assign UUIDv7 values as primary keys. UUIDv7 provides
 * globally unique, time-ordered identifiers that improve index locality
 * and query performance compared to random UUIDs.
 * </p>
 *
 * <p>
 * Usage example:
 * <pre>{@code
 * @Id
 * @GeneratedValue(generator = "uuid7")
 * @UUIDv7
 * private UUID id;
 * }</pre>
 * </p>
 *
 * <p>
 * Target:
 * <ul>
 *   <li>Can be applied to fields.</li>
 *   <li>Can be applied to getter methods.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Retention:
 * <ul>
 *   <li>Runtime retention ensures Hibernate can process the annotation
 *   during entity lifecycle operations.</li>
 * </ul>
 * </p>
 *
 * @see UUIDv7Generator
 * @see org.hibernate.annotations.IdGeneratorType
 *
 * @author BankcoreTeam - Sebastian Orjuela
 * @version 1.0
 */
@IdGeneratorType(UUIDv7Generator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UUIDv7 {
}
