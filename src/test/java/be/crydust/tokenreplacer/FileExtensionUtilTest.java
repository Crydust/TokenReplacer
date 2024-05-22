package be.crydust.tokenreplacer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class FileExtensionUtilTest {
    @Test
    void shouldReplaceExtension() {
        Path original = Paths.get("c:/temp/numbers.txt");
        Path expected = Paths.get("c:/temp/numbers.csv");

        Path actual = FileExtensionUtil.replaceExtension(original, "csv");

        assertThat(actual, Matchers.is(expected));
    }

    @Test
    void shouldReplaceExtensionAndIgnoreDot() {
        Path original = Paths.get("c:/temp/numbers.txt");
        Path expected = Paths.get("c:/temp/numbers.csv");

        Path actual = FileExtensionUtil.replaceExtension(original, ".csv");

        assertThat(actual, Matchers.is(expected));
    }

    @Test
    void shouldRemoveExtension() {
        Path original = Paths.get("c:/temp/numbers.txt");
        Path expected = Paths.get("c:/temp/numbers");

        Path actual = FileExtensionUtil.replaceExtension(original, "");

        assertThat(actual, Matchers.is(expected));
    }

    @Test
    void shouldThrowWhenFileDoesNotHaveExtension() {
        Path original = Paths.get("c:/temp/numbers");
        assertThrows(IllegalArgumentException.class, () -> FileExtensionUtil.replaceExtension(original, "csv"));
    }

    @Test
    void shouldThrowWhenFileHasEmptyExtension() {
        Path original = Paths.get("c:/temp/numbers.");
        assertThrows(IllegalArgumentException.class, () -> FileExtensionUtil.replaceExtension(original, "csv"));
    }
}
