package com.bankcore.accounts.utils.validators.pin;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtmPinValidator")

public class AtmPinValidatorTest {

    AtmPinValidator validator;

    @Mock
    ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new AtmPinValidator();
    }

    // ─── null ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when pin is null")
    class NullPin {

        @Test
        @DisplayName("returns true (null is delegated to @NotNull)")
        void shouldReturnTrueForNull() {
            assertThat(validator.isValid(null, context)).isTrue();
        }
    }

    // ─── valid pins ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when pin is valid")
    class ValidPin {

        @Test
        @DisplayName("returns true for a pin with all unique digits")
        void shouldReturnTrueForAllUniqueDigits() {
            assertThat(validator.isValid("1234", context)).isTrue();
        }

        @Test
        @DisplayName("returns true when a digit appears exactly 3 times")
        void shouldReturnTrueWhenDigitAppearsThreeTimes() {
            // '1' appears 3 times — boundary value, still valid
            assertThat(validator.isValid("111234", context)).isTrue();
        }

        @Test
        @DisplayName("returns true when multiple digits each appear up to 3 times")
        void shouldReturnTrueWhenMultipleDigitsAppearThreeTimes() {
            // '1' x3, '2' x3
            assertThat(validator.isValid("111222", context)).isTrue();
        }

        @ParameterizedTest(name = "pin: \"{0}\"")
        @ValueSource(strings = {"0011223", "123123", "999888", "121212"})
        @DisplayName("returns true for various valid pins")
        void shouldReturnTrueForVariousValidPins(String pin) {
            assertThat(validator.isValid(pin, context)).isTrue();
        }

        @Test
        @DisplayName("returns true for an empty string (no digits to violate the rule)")
        void shouldReturnTrueForEmptyString() {
            assertThat(validator.isValid("", context)).isTrue();
        }
    }

    // ─── invalid pins ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when pin is invalid")
    class InvalidPin {

        @Test
        @DisplayName("returns false when a digit appears exactly 4 times")
        void shouldReturnFalseWhenDigitAppearsFourTimes() {
            // '1' appears 4 times — first violation of the rule
            assertThat(validator.isValid("1111", context)).isFalse();
        }

        @Test
        @DisplayName("returns false when a digit appears more than 4 times")
        void shouldReturnFalseWhenDigitAppearsMoreThanFourTimes() {
            assertThat(validator.isValid("11111", context)).isFalse();
        }

        @Test
        @DisplayName("returns false when repeated digit is not the first character")
        void shouldReturnFalseWhenRepeatedDigitIsNotFirst() {
            // '2' appears 4 times, preceded by other digits
            assertThat(validator.isValid("12222", context)).isFalse();
        }

        @Test
        @DisplayName("returns false when repeated digit is at the end")
        void shouldReturnFalseWhenRepeatedDigitIsAtEnd() {
            assertThat(validator.isValid("12344444", context)).isFalse();
        }

        @ParameterizedTest(name = "pin: \"{0}\"")
        @ValueSource(strings = {"00001", "99999", "11112222", "123444456"})
        @DisplayName("returns false for various pins with a digit exceeding 3 occurrences")
        void shouldReturnFalseForVariousInvalidPins(String pin) {
            assertThat(validator.isValid(pin, context)).isFalse();
        }
    }

    // ─── boundary: exactly 3 vs 4 occurrences ────────────────────────────────

    @Nested
    @DisplayName("boundary — 3 vs 4 occurrences of the same digit")
    class Boundary {

        @Test
        @DisplayName("3 occurrences is the maximum allowed (returns true)")
        void threeOccurrencesIsValid() {
            assertThat(validator.isValid("111", context)).isTrue();
        }

        @Test
        @DisplayName("4 occurrences exceeds the limit (returns false)")
        void fourOccurrencesIsInvalid() {
            assertThat(validator.isValid("1111", context)).isFalse();
        }
    }
}
