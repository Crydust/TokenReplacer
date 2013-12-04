package be.crydust.tokenreplacer;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TokenReplacerTest {

    @Test
    public void testReplacetokensWithNoMatches() {
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
    public void testReplacetokensWithOneMatch() {
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
    public void testReplacetokensWithMatchAtStartMiddleEnd() {
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
    public void testReplacetokensWithWierdCharacters() {
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
    public void testReplacetokensWithMultipleKeys() {
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
    public void testReplacetokensWithMultipleKeysAndEqualBeginEndToken() {
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
    public void testReplacetokensWithDotTokens() {
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
    public void testReplacetokensWithBackslashAndDollar() {
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
