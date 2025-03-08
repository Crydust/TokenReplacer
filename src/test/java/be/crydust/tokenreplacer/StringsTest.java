package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StringsTest {
    @Test
    void shouldThrowNullPointerExceptionWhenStringIsNull() {
        assertThrows(NullPointerException.class, () -> Strings.requireNonEmpty(null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenStringIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Strings.requireNonEmpty(""));
    }

    @Test
    void shouldDoNothingWhenStringIsNeitherNullNorEmpty() {
        Strings.requireNonEmpty("foo");
    }

    @Test
    void shouldDoNothingWhenStringIsBlank() {
        Strings.requireNonEmpty(" ");
    }
}
