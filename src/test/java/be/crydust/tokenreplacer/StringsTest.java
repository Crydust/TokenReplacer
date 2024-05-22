package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringsTest {
    @Test
    void shouldThrowNullPointerExceptionWhenStringIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> Strings.requireNonEmpty(null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenStringIsEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Strings.requireNonEmpty(""));
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
