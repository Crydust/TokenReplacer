package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.InstanceOfAssertFactories.ARRAY;
import static org.junit.jupiter.api.Assertions.assertAll;

class AppReadConfigTest {

    @Test
    void readConfigNull() {
        String[] args = new String[0];
        assertThatExceptionOfType(ReadConfigFailed.class)
                .isThrownBy(() -> App.readConfig(args))
                .withMessage("Provide at least one -D or -r argument.");
    }

    @Test
    void readConfigWithMissingArgument() {
        String[] args = {"-b"};
        assertThatExceptionOfType(ReadConfigFailed.class)
                .isThrownBy(() -> App.readConfig(args))
                .withMessage("Configuration not valid.")
                .havingCause()
                .withMessage("Missing argument for option: b");
    }

    @Test
    void readConfigSimple() throws Exception {
        String[] args = "-D a=b".split(" ");

        Config result = App.readConfig(args);

        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.begintoken()).isEqualTo("@"),
                () -> assertThat(result.endtoken()).isEqualTo("@"),
                () -> assertThat(result.folder()).isEqualTo(Paths.get(System.getProperty("user.dir"))),
                () -> assertThat(result.quiet()).isEqualTo(false),
                () -> assertThat(result.replacetokens()).containsExactly(entry("a", "b")),
                () -> assertThat(result.excludes()).isEmpty()
        );
    }

    @Test
    void readConfigIncludeAndExclude() throws Exception {
        String[] args = "-D a=b -exclude **/tmp/**".split(" ");
        Config result = App.readConfig(args);
        assertThat(result.excludes()).containsExactly("**/tmp/**");
    }

    @Test
    void readConfigIncludeAndExcludeMultiple() throws Exception {
        String[] args = "-D a=b -exclude **/tmp/** -exclude **/0,1,2.zzz".split(" ");
        Config result = App.readConfig(args);
        assertThat(result.excludes()).containsExactly("**/tmp/**", "**/0,1,2.zzz");
    }

}
