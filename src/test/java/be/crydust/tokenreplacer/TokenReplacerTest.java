package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenReplacerTest {

    @Test
    void replacetokensWithEmptyReplacetokens() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = Map.of();
        assertThrows(IllegalArgumentException.class, () -> new TokenReplacer(begintoken, endtoken, replacetokens));
    }

    @Test
    void replacetokensWithNoMatches() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = Map.of("DATE", "today");
        String input = "Lorem ipsum";
        String expected = "Lorem ipsum";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual, is(expected));
    }

    @Test
    void replacetokensWithOneMatch() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = Map.of("DATE", "today");
        String input = "Lorem <DATE> ipsum";
        String expected = "Lorem today ipsum";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual, is(expected));
    }

    @Test
    void replacetokensWithMatchAtStartMiddleEnd() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = Map.of("DATE", "today");
        String input = "<DATE> Lorem <DATE> ipsum <DATE>";
        String expected = "today Lorem today ipsum today";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual, is(expected));
    }

    @Test
    void replacetokensWithWierdCharacters() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = Map.of("²&é\"'(§è!çà)-^$ùµ,;:=<>'", "today");
        String input = "<²&é\"'(§è!çà)-^$ùµ,;:=<>'> Lorem <²&é\"'(§è!çà)-^$ùµ,;:=<>'> ipsum <²&é\"'(§è!çà)-^$ùµ,;:=<>'>";
        String expected = "today Lorem today ipsum today";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual, is(expected));
    }

    @Test
    void replacetokensWithMultipleKeys() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = Map.of(
                "a", "A",
                "b", "B",
                "c", "C",
                "d", "D",
                "e", "E");
        String input = "<a> Lorem <b> ipsum <c>";
        String expected = "A Lorem B ipsum C";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual, is(expected));
    }

    @Test
    void replacetokensWithMultipleKeysAndEqualBeginEndToken() {
        String begintoken = "@";
        String endtoken = "@";
        Map<String, String> replacetokens = Map.of(
                "a", "A",
                "b", "B",
                "c", "C",
                "d", "D",
                "e", "E");
        String input = "@a@ Lorem @b@ ipsum @c@";
        String expected = "A Lorem B ipsum C";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual, is(expected));
    }

    @Test
    void replacetokensWithDotTokens() {
        String begintoken = ".";
        String endtoken = ".";
        Map<String, String> replacetokens = Map.of(
                "a", "A",
                "b", "B",
                "c", "C",
                "d", "D",
                "e", "E");
        String input = ".a. Lorem .b. ipsum .c.";
        String expected = "A Lorem B ipsum C";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual, is(expected));
    }

    @Test
    void replacetokensWithBackslashAndDollar() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = Map.of(
                "a", "a\\a",
                "b", "b$b",
                "c", "c<c");
        String input = "<a> Lorem <b> ipsum <c>";
        String expected = "a\\a Lorem b$b ipsum c<c";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual, is(expected));
    }
}
