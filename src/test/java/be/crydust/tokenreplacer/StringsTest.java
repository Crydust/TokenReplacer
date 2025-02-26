package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

class StringsTest {
    @Test
    void shouldThrowNullPointerExceptionWhenStringIsNull() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Strings.requireNonEmpty(null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenStringIsEmpty() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> Strings.requireNonEmpty(""));
    }

    @Test
    void shouldDoNothingWhenStringIsNeitherNullNorEmpty() {
        assertThatNoException().isThrownBy(() -> Strings.requireNonEmpty("foo"));
    }

    @Test
    void shouldDoNothingWhenStringIsBlank() {
        assertThatNoException().isThrownBy(() -> Strings.requireNonEmpty(" "));
    }
}
