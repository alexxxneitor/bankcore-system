package com.bankcore.accounts.services;

import com.bankcore.accounts.services.complements.IbanGeneratorService;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class IbanGeneratorServiceTest {

    private final IbanGeneratorService ibanGeneratorService =
            new IbanGeneratorService();

    @Test
    void shouldGenerateSpanishIbanWithCorrectFormat() {

        String iban = ibanGeneratorService.generateSpanishIban();

        assertNotNull(iban);
        assertEquals(24, iban.length());
        assertTrue(iban.startsWith("ES"));

        // Verify that remaining characters are digits
        assertTrue(iban.substring(2).matches("\\d+"));
    }

    @Test
    void shouldGenerateValidIbanAccordingToMod97() {

        String iban = ibanGeneratorService.generateSpanishIban();

        // Rearrange for validation (move first 4 chars to the end)
        String rearranged = iban.substring(4) + iban.substring(0, 4);

        String numeric = convertLettersToNumbers(rearranged);

        BigInteger bigInt = new BigInteger(numeric);
        int mod = bigInt.mod(BigInteger.valueOf(97)).intValue();

        assertEquals(1, mod); // Valid IBAN must return 1
    }

    @Test
    void shouldGenerateDifferentIbansOnMultipleCalls() {

        String iban1 = ibanGeneratorService.generateSpanishIban();
        String iban2 = ibanGeneratorService.generateSpanishIban();

        assertNotEquals(iban1, iban2);
    }

    // Helper method for test validation
    private String convertLettersToNumbers(String input) {
        StringBuilder result = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (Character.isLetter(ch)) {
                char upper = Character.toUpperCase(ch);
                result.append(upper - 'A' + 10);
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
