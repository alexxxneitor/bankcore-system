package com.bankcore.accounts.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

public class IbanGeneratorService {

    private static final SecureRandom random = new SecureRandom();

    public String generateSpanishIban() {
        String countryCode = "ES";

        String ccc = generateRandomDigits(20);

        String tempIban = ccc + countryCode + "00";

        String numericIban = convertLettersToNumbers(tempIban);

        BigInteger bigInt = new BigInteger(numericIban);
        int mod = bigInt.mod(BigInteger.valueOf(97)).intValue();

        int checkDigits = 98 - mod;

        return countryCode + String.format("%02d", checkDigits) + ccc;
    }

    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String convertLettersToNumbers(String input) {
        StringBuilder result = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (Character.isLetter(ch)) {
                result.append((int) ch - 55);
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
