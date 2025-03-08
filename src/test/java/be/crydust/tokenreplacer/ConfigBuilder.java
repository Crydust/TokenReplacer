package be.crydust.tokenreplacer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ConfigBuilder {
    private final Path folder;
    private String begintoken = "@";
    private String endtoken = "@";
    private Map<String, String> replacetokens = Map.of("a", "A");
    private boolean quiet = true;
    private List<String> excludes = new ArrayList<>();

    private ConfigBuilder(Path folder) {
        this.folder = folder;
    }

    public static ConfigBuilder aConfig(Path folder) {
        return new ConfigBuilder(folder);
    }

    public ConfigBuilder withBeginToken(String begintoken) {
        this.begintoken = begintoken;
        return this;
    }

    public ConfigBuilder withEndToken(String endtoken) {
        this.endtoken = endtoken;
        return this;
    }

    public ConfigBuilder withReplaceTokens(Map<String, String> replacetokens) {
        this.replacetokens = Map.copyOf(replacetokens);
        return this;
    }

    public ConfigBuilder withQuiet(boolean quiet) {
        this.quiet = quiet;
        return this;
    }

    public ConfigBuilder withExcludes(String... excludes) {
        this.excludes = List.of(excludes);
        return this;
    }

    public Config build() {
        return new Config(begintoken, endtoken, replacetokens, folder, quiet, excludes.toArray(new String[0]));
    }
}
