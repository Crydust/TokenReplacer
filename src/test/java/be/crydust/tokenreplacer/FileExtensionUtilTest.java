package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static be.crydust.tokenreplacer.CustomThrowableMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileExtensionUtilTest {
    @Test
    void shouldReplaceExtension() {
        Path original = Paths.get("c:/temp/numbers.txt");
        Path expected = Paths.get("c:/temp/numbers.csv");

        Path actual = FileExtensionUtil.replaceExtension(original, "csv");

        assertThat(actual, is(expected));
    }

    @Test
    void shouldReplaceExtensionAndIgnoreDot() {
        Path original = Paths.get("c:/temp/numbers.txt");
        Path expected = Paths.get("c:/temp/numbers.csv");

        Path actual = FileExtensionUtil.replaceExtension(original, ".csv");

        assertThat(actual, is(expected));
    }

    @Test
    void shouldRemoveExtension() {
        Path original = Paths.get("c:/temp/numbers.txt");
        Path expected = Paths.get("c:/temp/numbers");

        Path actual = FileExtensionUtil.replaceExtension(original, "");

        assertThat(actual, is(expected));
    }

    @Test
    void shouldThrowWhenFileDoesNotHaveExtension() {
        Path original = Paths.get("c:/temp/numbers");
        var e = assertThrows(IllegalArgumentException.class, () -> FileExtensionUtil.replaceExtension(original, "csv"));
        assertThat(e, hasMessage("path has no extension"));
    }

    @Test
    void shouldThrowWhenFileHasEmptyExtension() {
        Path original = Paths.get("c:/temp/numbers.");
        var e = assertThrows(IllegalArgumentException.class, () -> FileExtensionUtil.replaceExtension(original, "csv"));
        assertThat(e, hasMessage("path has no extension"));
    }
}
