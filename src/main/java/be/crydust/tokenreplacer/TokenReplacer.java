package be.crydust.tokenreplacer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kristof
 */
public class TokenReplacer {

    private final String begintoken;
    private final String endtoken;
    private final Map<String, String> replacetokens;

    private Pattern pattern;

    /**
     *
     * @param begintoken
     * @param endtoken
     * @param replacetokens
     */
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
            StringBuilder sb = new StringBuilder();
            sb.append(Pattern.quote(begintoken));
            sb.append("(");
            for (String key : replacetokens.keySet()) {
                sb.append(Pattern.quote(key));
                sb.append("|");
            }
            sb.setLength(sb.length() - 1);
            sb.append(")");
            sb.append(Pattern.quote(endtoken));
            pattern = Pattern.compile(sb.toString());
        }
        return pattern;
    }

    /**
     * replaces all occurrences of "begintoken key endtoken" by "value"
     *
     * @param input
     * @return
     */
    public String replace(final String input) {
        final Matcher matcher = getPattern().matcher(input);
        final StringBuilder sb = new StringBuilder();
        int position = 0;
        while(matcher.find()) {
            // appendReplacement
            sb.append(input, position, matcher.start());
            position = matcher.end();
            final String replacement = replacetokens.get(matcher.group(1));
            sb.append(replacement);
        }
        // appendTail
        sb.append(input, position, input.length());
        return sb.toString();
    }
}
