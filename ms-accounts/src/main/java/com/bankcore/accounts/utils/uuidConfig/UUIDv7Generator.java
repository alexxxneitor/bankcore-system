package com.bankcore.accounts.utils.uuidConfig;

import com.github.f4b6a3.uuid.UuidCreator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.util.UUID;

public class UUIDv7Generator implements IdentifierGenerator {

    @Override
    public UUID generate(SharedSessionContractImplementor session, Object object) {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
