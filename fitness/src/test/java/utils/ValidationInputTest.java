package utils;

import kg.biamino.projects.utils.ValidationInput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationInputTest {

    @Test
    void nullChecker_nullValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ValidationInput.nullChecker(null, "field"));
    }

    @Test
    void nullChecker_nonNullValue_doesNotThrow() {
        assertDoesNotThrow(() -> ValidationInput.nullChecker("value", "field"));
    }

    @Test
    void integerChecker_lessThanOne_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ValidationInput.integerChecker(0, "field"));
        assertThrows(IllegalArgumentException.class, () -> ValidationInput.integerChecker(-5, "field"));
    }

    @Test
    void integerChecker_oneOrGreater_doesNotThrow() {
        assertDoesNotThrow(() -> ValidationInput.integerChecker(1, "field"));
        assertDoesNotThrow(() -> ValidationInput.integerChecker(100, "field"));
    }

    @Test
    void stringChecker_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ValidationInput.stringChecker(null, "field"));
    }

    @Test
    void stringChecker_blank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ValidationInput.stringChecker("   ", "field"));
    }

    @Test
    void stringChecker_nonBlank_doesNotThrow() {
        assertDoesNotThrow(() -> ValidationInput.stringChecker("value", "field"));
    }
}
