package be.crydust.tokenreplacer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TokenReplacerTest {

    @Test
    void testReplacetokensWithEmptyReplacetokens() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = Collections.emptyMap();
        assertThrows(IllegalArgumentException.class, () -> new TokenReplacer(begintoken, endtoken, replacetokens));
    }

    @Test
    void testReplacetokensWithNoMatches() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("DATE", "today");
        String input = "Lorem ipsum";
        String expected = "Lorem ipsum";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual, is(expected));
    }

    @Test
    void testReplacetokensWithOneMatch() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("DATE", "today");
        String input = "Lorem <DATE> ipsum";
        String expected = "Lorem today ipsum";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual, is(expected));
    }

    @Test
    void testReplacetokensWithMatchAtStartMiddleEnd() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("DATE", "today");
        String input = "<DATE> Lorem <DATE> ipsum <DATE>";
        String expected = "today Lorem today ipsum today";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual, is(expected));
    }

    @Test
    void testReplacetokensWithWierdCharacters() {
        String begintoken = "<";
        String endtoken = ">";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("²&é\"'(§è!çà)-^$ùµ,;:=<>'", "today");
        String input = "<²&é\"'(§è!çà)-^$ùµ,;:=<>'> Lorem <²&é\"'(§è!çà)-^$ùµ,;:=<>'> ipsum <²&é\"'(§è!çà)-^$ùµ,;:=<>'>";
        String expected = "today Lorem today ipsum today";
        TokenReplacer cut = new TokenReplacer(begintoken, endtoken, replacetokens);
        String actual = cut.replace(input);
        assertThat(actual, is(expected));
    }

    @Test
    void testReplacetokensWithMultipleKeys() {
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
        assertThat(actual, is(expected));
    }

    @Test
    void testReplacetokensWithMultipleKeysAndEqualBeginEndToken() {
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
        assertThat(actual, is(expected));
    }

    @Test
    void testReplacetokensWithDotTokens() {
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
        assertThat(actual, is(expected));
    }

    @Test
    void testReplacetokensWithBackslashAndDollar() {
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
        assertThat(actual, is(expected));
    }
}
