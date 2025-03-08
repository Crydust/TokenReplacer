package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static be.crydust.tokenreplacer.CustomThrowableMatchers.hasCauseMessage;
import static be.crydust.tokenreplacer.CustomThrowableMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppReadConfigTest {

    @Test
    void readConfigNull() {
        String[] args = new String[0];
        var e = assertThrows(ReadConfigFailed.class, () -> App.readConfig(args));
        assertThat(e, hasMessage("Provide at least one -D or -r argument."));
    }

    @Test
    void readConfigWithMissingArgument() {
        String[] args = {"-b"};
        var e = assertThrows(ReadConfigFailed.class, () -> App.readConfig(args));
        assertThat(e, allOf(
                hasMessage("Configuration not valid."),
                hasCauseMessage("Missing argument for option: b")));
    }

    @Test
    void readConfigSimple() throws Exception {
        String[] args = "-D a=b".split(" ");

        Config result = App.readConfig(args);

        assertAll(
                () -> assertThat(result, notNullValue()),
                () -> assertThat(result.begintoken(), is("@")),
                () -> assertThat(result.endtoken(), is("@")),
                () -> assertThat(result.folder(), is(Path.of(System.getProperty("user.dir")))),
                () -> assertThat(result.quiet(), is(false)),
                () -> assertThat(result.replacetokens(), allOf(aMapWithSize(1), hasEntry("a", "b"))),
                () -> assertThat(result.excludes(), emptyArray())
        );
    }

    @Test
    void readConfigIncludeAndExclude() throws Exception {
        String[] args = "-D a=b -exclude **/tmp/**".split(" ");
        Config result = App.readConfig(args);
        assertThat(result.excludes(), arrayContaining("**/tmp/**"));
    }

    @Test
    void readConfigIncludeAndExcludeMultiple() throws Exception {
        String[] args = "-D a=b -exclude **/tmp/** -exclude **/0,1,2.zzz".split(" ");
        Config result = App.readConfig(args);
        assertThat(result.excludes(), arrayContaining("**/tmp/**", "**/0,1,2.zzz"));
    }

}
