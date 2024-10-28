package be.crydust.tokenreplacer;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.joining;

/**
 * The application configuration. A dumb value object. The toString method returns a user-friendly summary.
 *
 * @param begintoken string that precedes the key to replace
 * @param endtoken string that follows the key to replace
 * @param replacetokens key-value pairs to replace
 * @param folder base directory to start replacing
 * @param quiet true if no confirmation should be asked
 * @param excludes patterns to exclude from replacement
 */
public record Config(
        @Nonnull String begintoken,
        @Nonnull String endtoken,
        @Nonnull Map<String, String> replacetokens,
        @Nonnull Path folder,
        boolean quiet,
        @Nonnull String[] excludes
) {

    @Override
    public String toString() {
        String replacetokensSB = replacetokens.entrySet().stream()
                .sorted(comparingByKey())
                .map(it -> "\n    %s=%s".formatted(it.getKey(), it.getValue()))
                .collect(joining(",", "{", "\n  }"));

        String excludesSB = Arrays.stream(excludes)
                .map(it -> it.replace(",", "\\,"))
                .collect(joining(",", "[", "]"));

        return String.format("""
                Config{
                  begintoken=%s,
                  endtoken=%s,
                  replacetokens=%s,
                  folder=%s,
                  quiet=%s,
                  excludes=%s
                }""", begintoken, endtoken, replacetokensSB, folder, quiet, excludesSB);
    }

}
