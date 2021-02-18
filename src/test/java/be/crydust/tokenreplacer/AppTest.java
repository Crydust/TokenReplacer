package be.crydust.tokenreplacer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void testReadConfigNull() {
        String[] args = new String[0];
        Config result = App.readConfig(args);
        assertThat(result, is(nullValue()));
    }

    @Test
    void testReadConfigSimple() {
        String[] args = "-D a=b".split(" ");
        Config result = App.readConfig(args);
        assertThat(result, is(not(nullValue())));
        assertThat(result.getBegintoken(), is("@"));
        assertThat(result.getEndtoken(), is("@"));
        //assertThat(result.getFolder(), is(???));
        assertThat(result.isQuiet(), is(false));
        assertThat(result, is(not(nullValue())));
        assertThat(result.getReplacetokens().size(), is(1));
        assertThat(result.getReplacetokens(), hasEntry("a", "b"));
        assertThat(result.getExcludes(), is(emptyArray()));
    }

    @Test
    void testReadConfigIncludeAndExclude() {
        String[] args = "-D a=b -exclude **/tmp/**".split(" ");
        Config result = App.readConfig(args);
        assertThat(result, is(not(nullValue())));
        assertThat(result.getExcludes(), is(new String[]{
            "**/tmp/**"
        }));
    }

    @Test
    void testReadConfigIncludeAndExcludeMultiple() {
        String[] args = "-D a=b -exclude **/tmp/** -exclude **/0,1,2.zzz".split(" ");
        Config result = App.readConfig(args);
        assertThat(result, is(not(nullValue())));
        assertThat(result.getExcludes(), is(new String[]{
            "**/tmp/**", "**/0,1,2.zzz"
        }));
    }

}
