package com.bankcore.accounts.config;

import com.bankcore.accounts.utils.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProviderService {

    private final JwtEncoder jwtEncoder;

    /**
     * Generates a JWT token for a given user.
     *
     * @return signed JWT token
     */
    public String generateServiceToken() {

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("bankcore")
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .subject("BANKCORE_SYSTEM_ACCOUNTS")
                .claim("roles", List.of(UserRole.SERVICE.name()))
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }
}
