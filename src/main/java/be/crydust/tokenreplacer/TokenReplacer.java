package be.crydust.tokenreplacer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.joining;

public final class TokenReplacer {

    private final String begintoken;
    private final String endtoken;
    private final Map<String, String> replacetokens;

    private Pattern pattern;

    public TokenReplacer(String begintoken, String endtoken, Map<String, String> replacetokens) {
        Strings.requireNonEmpty(begintoken);
        Strings.requireNonEmpty(endtoken);
        Objects.requireNonNull(replacetokens);
        if (replacetokens.isEmpty()) {
            throw new IllegalArgumentException("replacetokens");
        }
        for (String key : replacetokens.keySet()) {
            Strings.requireNonEmpty(key);
        }
        this.begintoken = begintoken;
        this.endtoken = endtoken;
        this.replacetokens = new HashMap<>(replacetokens);
    }

    private Pattern getPattern() {
        if (pattern == null) {
            String regex = replacetokens.keySet().stream()
                    .map(Pattern::quote)
                    .collect(joining(
                            "|",
                            quote(begintoken) + "(",
                            ")" + quote(endtoken)
                    ));
            pattern = Pattern.compile(regex);
        }
        return pattern;
    }

    /**
     * replaces all occurrences of "begintoken key endtoken" by "value"
     */
    public String replace(String input) {
        Matcher matcher = getPattern().matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = replacetokens.get(key);
            matcher.appendReplacement(sb, quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
