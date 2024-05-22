package be.crydust.tokenreplacer;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.joining;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * The application configuration. A dumb value object. The toString method returns a user-friendly summary.
 */
public class Config {

    private final String begintoken;
    private final String endtoken;
    private final Map<String, String> replacetokens;
    private final Path folder;
    private final boolean quiet;
    private final String[] excludes;

    /**
     * @param begintoken string that precedes the key to replace
     * @param endtoken string that follows the key to replace
     * @param replacetokens key-value pairs to replace
     * @param folder base directory to start replacing
     * @param quiet true if no confirmation should be asked
     * @param excludes patterns to exclude from replacement
     */
    public Config(@Nonnull String begintoken, @Nonnull String endtoken, @Nonnull Map<String, String> replacetokens, @Nonnull Path folder, boolean quiet, @Nonnull String[] excludes) {
        Strings.requireNonEmpty(begintoken);
        Strings.requireNonEmpty(endtoken);
        Objects.requireNonNull(replacetokens);
        Objects.requireNonNull(folder);
        Objects.requireNonNull(excludes);
        this.begintoken = begintoken;
        this.endtoken = endtoken;
        this.replacetokens = replacetokens;
        this.folder = folder;
        this.quiet = quiet;
        this.excludes = excludes;
    }

    /**
     * @return string that precedes the key to replace
     */
    public String getBegintoken() {
        return begintoken;
    }

    /**
     * @return string that follows the key to replace
     */
    public String getEndtoken() {
        return endtoken;
    }

    /**
     * @return key-value pairs to replace
     */
    public Map<String, String> getReplacetokens() {
        return replacetokens;
    }

    /**
     * @return base directory to start replacing
     */
    public Path getFolder() {
        return folder;
    }

    /**
     * @return true if no confirmation should be asked
     */
    public boolean isQuiet() {
        return quiet;
    }

    /**
     * @return patterns to exclude from replacement
     */
    public String[] getExcludes() {
        return excludes;
    }

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
