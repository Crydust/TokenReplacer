package be.crydust.tokenreplacer;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.InstanceOfAssertFactories.ARRAY;

class AppReadConfigTest {

    @Test
    void readConfigNull() {
        String[] args = new String[0];
        assertThatExceptionOfType(ReadConfigFailed.class).isThrownBy(() -> App.readConfig(args));
    }

    @Test
    void readConfigSimple() throws Exception {
        String[] args = "-D a=b".split(" ");

        Config result = App.readConfig(args);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result).isNotNull();
        softly.assertThat(result.begintoken()).isEqualTo("@");
        softly.assertThat(result.endtoken()).isEqualTo("@");
        softly.assertThat(result.folder()).isEqualTo(Paths.get(System.getProperty("user.dir")));
        softly.assertThat(result.quiet()).isEqualTo(false);
        softly.assertThat(result.replacetokens()).containsExactly(entry("a", "b"));
        softly.assertThat(result.excludes()).isEmpty();
        softly.assertAll();

        // alternatives
        assertThat(result)
                .returns("@", Config::begintoken)
                .returns("@", Config::endtoken)
                .returns(Paths.get(System.getProperty("user.dir")), Config::folder)
                .returns(false, Config::quiet);
        assertThat(result)
                .usingRecursiveComparison()
                .comparingOnlyFields(
                        "begintoken",
                        "endtoken",
                        "replacetokens",
                        "folder",
                        "quiet",
                        "excludes"
                )
                .isEqualTo(new Config(
                        "@",
                        "@",
                        Map.of("a", "b"),
                        Paths.get(System.getProperty("user.dir")),
                        false,
                        new String[0]
                ));
        assertThat(result)
                .extracting(
                        "begintoken",
                        "endtoken",
                        "replacetokens",
                        "folder",
                        "quiet",
                        "excludes"
                )
                .containsExactly(
                        "@",
                        "@",
                        Map.of("a", "b"),
                        Paths.get(System.getProperty("user.dir")),
                        false,
                        new String[0]
                );
        assertThat(result)
                .satisfies(
                        arg -> assertThat(arg.begintoken()).isEqualTo( "@"),
                        arg -> assertThat(arg.endtoken()).isEqualTo( "@"),
                        arg -> assertThat(arg.replacetokens()).isEqualTo( Map.of("a", "b")),
                        arg -> assertThat(arg.folder()).isEqualTo( Paths.get(System.getProperty("user.dir"))),
                        arg -> assertThat(arg.quiet()).isEqualTo( false),
                        arg -> assertThat(arg.excludes()).isEqualTo( new String[0])
                );
    }

    @Test
    void readConfigIncludeAndExclude() throws Exception {
        String[] args = "-D a=b -exclude **/tmp/**".split(" ");
        Config result = App.readConfig(args);
        assertThat(result.excludes()).containsExactly("**/tmp/**");

        // alternatives
        assertThat(result).extracting(Config::excludes, as(ARRAY)).containsExactly("**/tmp/**");
        assertThat(result).extracting("excludes", as(ARRAY)).containsExactly("**/tmp/**");
        assertThat(result).isNotNull()
                .extracting(Config::excludes, as(ARRAY)).containsExactly("**/tmp/**");
    }

    @Test
    void readConfigIncludeAndExcludeMultiple() throws Exception {
        String[] args = "-D a=b -exclude **/tmp/** -exclude **/0,1,2.zzz".split(" ");
        Config result = App.readConfig(args);
        assertThat(result.excludes()).containsExactly("**/tmp/**", "**/0,1,2.zzz");
    }

}
