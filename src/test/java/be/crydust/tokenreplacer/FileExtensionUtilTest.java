package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class FileExtensionUtilTest {
    @Test
    void shouldReplaceExtension() {
        Path original = Paths.get("c:/temp/numbers.txt");
        Path expected = Paths.get("c:/temp/numbers.csv");

        Path actual = FileExtensionUtil.replaceExtension(original, "csv");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReplaceExtensionAndIgnoreDot() {
        Path original = Paths.get("c:/temp/numbers.txt");
        Path expected = Paths.get("c:/temp/numbers.csv");

        Path actual = FileExtensionUtil.replaceExtension(original, ".csv");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldRemoveExtension() {
        Path original = Paths.get("c:/temp/numbers.txt");
        Path expected = Paths.get("c:/temp/numbers");

        Path actual = FileExtensionUtil.replaceExtension(original, "");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldThrowWhenFileDoesNotHaveExtension() {
        Path original = Paths.get("c:/temp/numbers");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> FileExtensionUtil.replaceExtension(original, "csv"))
                .withMessage("path has no extension");
    }

    @Test
    void shouldThrowWhenFileHasEmptyExtension() {
        Path original = Paths.get("c:/temp/numbers.");
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> FileExtensionUtil.replaceExtension(original, "csv"))
                .withMessage("path has no extension");
    }
}
