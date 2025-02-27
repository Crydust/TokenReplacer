package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class TokenReplacerTest {

    @Test
    void replacetokensWithEmptyReplacetokens() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = Map.of();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new TokenReplacer(begintoken, endtoken, replacetokens));
    }

    @Test
    void replacetokensWithNoMatches() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("DATE", "today");
        String input = "Lorem ipsum";
        String expected = "Lorem ipsum";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void replacetokensWithOneMatch() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("DATE", "today");
        String input = "Lorem <DATE> ipsum";
        String expected = "Lorem today ipsum";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void replacetokensWithMatchAtStartMiddleEnd() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("DATE", "today");
        String input = "<DATE> Lorem <DATE> ipsum <DATE>";
        String expected = "today Lorem today ipsum today";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void replacetokensWithWierdCharacters() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("²&é\"'(§è!çà)-^$ùµ,;:=<>'", "today");
        String input = "<²&é\"'(§è!çà)-^$ùµ,;:=<>'> Lorem <²&é\"'(§è!çà)-^$ùµ,;:=<>'> ipsum <²&é\"'(§è!çà)-^$ùµ,;:=<>'>";
        String expected = "today Lorem today ipsum today";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void replacetokensWithMultipleKeys() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("a", "A");
        replacetokens.put("b", "B");
        replacetokens.put("c", "C");
        replacetokens.put("d", "D");
        replacetokens.put("e", "E");
        String input = "<a> Lorem <b> ipsum <c>";
        String expected = "A Lorem B ipsum C";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void replacetokensWithMultipleKeysAndEqualBeginEndToken() {
        String begintoken = "@";
        String endtoken = "@";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("a", "A");
        replacetokens.put("b", "B");
        replacetokens.put("c", "C");
        replacetokens.put("d", "D");
        replacetokens.put("e", "E");
        String input = "@a@ Lorem @b@ ipsum @c@";
        String expected = "A Lorem B ipsum C";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void replacetokensWithDotTokens() {
        String begintoken = ".";
        String endtoken = ".";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("a", "A");
        replacetokens.put("b", "B");
        replacetokens.put("c", "C");
        replacetokens.put("d", "D");
        replacetokens.put("e", "E");
        String input = ".a. Lorem .b. ipsum .c.";
        String expected = "A Lorem B ipsum C";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void replacetokensWithBackslashAndDollar() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("a", "a\\a");
        replacetokens.put("b", "b$b");
        replacetokens.put("c", "c<c");
        String input = "<a> Lorem <b> ipsum <c>";
        String expected = "a\\a Lorem b$b ipsum c<c";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual).isEqualTo(expected);
    }
}
