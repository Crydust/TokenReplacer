package be.crydust.tokenreplacer;

import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

class AppReadReplacetokensTest {

    @Test
    void shouldReadReplaceTokensFromCommandLineWhenNoFileGiven() throws Exception {
        CommandLineStub commandLine = new CommandLineStub(
                null,
                Map.of(
                        "a", "b",
                        "c", "d",
                        "e", "f"));

        Map<String, String> replacetokens = App.readReplacetokens(commandLine);

        assertThat(replacetokens).containsExactlyInAnyOrderEntriesOf(Map.of(
                "a", "b",
                "c", "d",
                "e", "f"
        ));
    }

    @Test
    void shouldReadReplaceTokensFromFileWhenNoCommandLineGiven(@TempDir Path folder) throws Exception {
        Path replacetokensFile = folder.resolve("replacetokens.properties");
        Files.writeString(replacetokensFile, """
                a=b
                c=d
                e=f
                """);
        CommandLineStub commandLine = new CommandLineStub(replacetokensFile, null);

        Map<String, String> replacetokens = App.readReplacetokens(commandLine);

        assertThat(replacetokens).containsExactlyInAnyOrderEntriesOf(Map.of(
                "a", "b",
                "c", "d",
                "e", "f"
        ));
    }

    @Test
    void shouldReadReplaceTokensFromFileAndOverrideFromCommandLine(@TempDir Path folder) throws Exception {
        Path replacetokensFile = folder.resolve("replacetokens.properties");
        Files.writeString(replacetokensFile, """
                a=b
                c=d
                e=f
                """);
        CommandLineStub commandLine = new CommandLineStub(
                replacetokensFile,
                Map.of(
                        "c", "override d",
                        "g", "add h"));

        Map<String, String> replacetokens = App.readReplacetokens(commandLine);

        assertThat(replacetokens).containsExactlyInAnyOrderEntriesOf(Map.of(
                "a", "b",
                "c", "override d",
                "e", "f",
                "g", "add h"
        ));
    }

    private final static class CommandLineStub extends CommandLine {

        private final Path replacetokensFile;
        private final Map<String, String> d;

        private CommandLineStub(Path replacetokensFile, Map<String, String> d) {
            this.replacetokensFile = replacetokensFile;
            this.d = d;
        }

        @Override
        public boolean hasOption(String opt) {
            return switch (opt) {
                case "replacetokens" -> replacetokensFile != null;
                case "D" -> d != null && !d.isEmpty();
                default -> throw new IllegalStateException("Unexpected value: " + opt);
            };
        }

        @Override
        public String getOptionValue(String opt) {
            if (opt.equals("replacetokens")) {
                return requireNonNull(replacetokensFile).toString();
            }
            throw new IllegalStateException("Unexpected value: " + opt);
        }

        @Override
        public String getOptionValue(String opt, String defaultValue) {
            if (opt.equals("replacetokens")) {
                return replacetokensFile == null ? defaultValue : replacetokensFile.toString();
            }
            throw new IllegalStateException("Unexpected value: " + opt);
        }

        @Override
        public Properties getOptionProperties(String opt) {
            if (opt.equals("D")) {
                Properties properties = new Properties();
                d.forEach(properties::setProperty);
                return properties;
            }
            throw new IllegalStateException("Unexpected value: " + opt);
        }

    }
}
