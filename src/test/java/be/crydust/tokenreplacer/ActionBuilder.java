package be.crydust.tokenreplacer;

import org.slf4j.Logger;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Map;

import static be.crydust.tokenreplacer.ConfigBuilder.aConfig;

public final class ActionBuilder {
    private final ConfigBuilder configBuilder;
    private PrintStream out = null;
    private PrintStream err = null;
    private Logger log = null;

    private ActionBuilder(Path folder) {
        this.configBuilder = aConfig(folder);
    }

    public static ActionBuilder anAction(Path folder) {
        return new ActionBuilder(folder);
    }

    public ActionBuilder withBeginToken(String begintoken) {
        this.configBuilder.withBeginToken(begintoken);
        return this;
    }

    public ActionBuilder withEndToken(String endtoken) {
        this.configBuilder.withEndToken(endtoken);
        return this;
    }

    public ActionBuilder withReplaceTokens(Map<String, String> replacetokens) {
        this.configBuilder.withReplaceTokens(Map.copyOf(replacetokens));
        return this;
    }

    public ActionBuilder withQuiet(boolean quiet) {
        this.configBuilder.withQuiet(quiet);
        return this;
    }

    public ActionBuilder withExcludes(String... excludes) {
        this.configBuilder.withExcludes(excludes);
        return this;
    }

    public ActionBuilder withOut(PrintStream out) {
        this.out = out;
        return this;
    }

    public ActionBuilder withErr(PrintStream err) {
        this.err = err;
        return this;
    }

    public ActionBuilder withLog(Logger log) {
        this.log = log;
        return this;
    }

    public Action build() {
        return new Action(configBuilder.build(), out, err, log);
    }
}
