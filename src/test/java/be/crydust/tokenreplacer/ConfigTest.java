package be.crydust.tokenreplacer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ConfigTest {

    @Test
    void shouldStringifyMinimalConfig() {
        Config config = new Config(
                "@",
                "@",
                Map.of(),
                Path.of("c:\\temp"),
                true,
                new String[0]
        );

        String userFriendlyString = config.toString();

        assertThat(userFriendlyString, is("""
                Config{
                  begintoken=@,
                  endtoken=@,
                  replacetokens={
                  },
                  folder=c:\\temp,
                  quiet=true,
                  excludes=[]
                }"""));
    }

    @Test
    void shouldEscapeCommaInExcludes() {
        Config config = new Config(
                "@",
                "@",
                Map.of(),
                Path.of("c:\\temp"),
                true,
                new String[]{
                        "**/with , comma/**"
                }
        );

        String userFriendlyString = config.toString();

        assertThat(userFriendlyString, is("""
                Config{
                  begintoken=@,
                  endtoken=@,
                  replacetokens={
                  },
                  folder=c:\\temp,
                  quiet=true,
                  excludes=[**/with \\, comma/**]
                }"""));
    }

    @Test
    void shouldSortReplacetokensByKey() {
        Config config = new Config(
                "@",
                "@",
                Map.of(
                        "b", "1",
                        "c", "2",
                        "a", "0"
                ),
                Path.of("c:\\temp"),
                true,
                new String[0]
        );

        String userFriendlyString = config.toString();

        assertThat(userFriendlyString, is("""
                Config{
                  begintoken=@,
                  endtoken=@,
                  replacetokens={
                    a=0,
                    b=1,
                    c=2
                  },
                  folder=c:\\temp,
                  quiet=true,
                  excludes=[]
                }"""));
    }

    @Test
    void shouldStringifyCompleteConfig() {
        Config config = new Config(
                "<",
                ">",
                Map.of(
                        "a0", "b0",
                        "a1", "b1"
                ),
                Path.of("c:\\temp"),
                false,
                new String[]{
                        "**/tmp/**",
                        "**/temp/**",
                        "**/with , comma/**"
                }
        );

        String userFriendlyString = config.toString();

        assertThat(userFriendlyString, is("""
                Config{
                  begintoken=<,
                  endtoken=>,
                  replacetokens={
                    a0=b0,
                    a1=b1
                  },
                  folder=c:\\temp,
                  quiet=false,
                  excludes=[**/tmp/**,**/temp/**,**/with \\, comma/**]
                }"""));
    }

}
