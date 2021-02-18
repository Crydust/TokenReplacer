package be.crydust.tokenreplacer;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * The application configuration. A dumb value object.
 *
 * @author kristof
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
        StringBuilder replacetokensSB = new StringBuilder();
        replacetokensSB.append("{");
        for (Map.Entry<String, String> replacetoken : replacetokens.entrySet()) {
            replacetokensSB
                    .append("\n    ")
                    .append(replacetoken.getKey())
                    .append('=')
                    .append(replacetoken.getValue())
                    .append(",");
        }
        if (replacetokensSB.length() > 1) {
            replacetokensSB.setLength(replacetokensSB.length() - 1);
        }
        replacetokensSB.append("\n  }");

        StringBuilder excludesSB = new StringBuilder();
        excludesSB.append("[");
        for (String exclude : excludes) {
            excludesSB.append(exclude.replace(",", "\\,")).append(",");
        }
        if (excludesSB.length() > 1) {
            excludesSB.setLength(excludesSB.length() - 1);
        }
        excludesSB.append("]");

        return String.format(""
                + "Config{\n"
                + "  begintoken=%s,\n"
                + "  endtoken=%s,\n"
                + "  replacetokens=%s,\n"
                + "  folder=%s,\n"
                + "  quiet=%s\n"
                + "  excludes=%s\n"
                + "}", begintoken, endtoken, replacetokensSB, folder, quiet, excludesSB);
    }

}
