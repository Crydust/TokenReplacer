package be.crydust.tokenreplacer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AppReadConfigTest {

    @Test
    void testReadConfigNull() {
        String[] args = new String[0];
        Assertions.assertThrows(ReadConfigFailed.class, () -> App.readConfig(args));
    }

    @Test
    void testReadConfigSimple() throws Exception {
        String[] args = "-D a=b".split(" ");
        Config result = App.readConfig(args);
        assertThat(result, is(not(nullValue())));
        assertThat(result.begintoken(), is("@"));
        assertThat(result.endtoken(), is("@"));
        assertThat(result.folder(), is(Paths.get(System.getProperty("user.dir"))));
        assertThat(result.quiet(), is(false));
        assertThat(result.replacetokens(), both(aMapWithSize(1)).and(hasEntry("a", "b")));
        assertThat(result.excludes(), is(emptyArray()));
    }

    @Test
    void testReadConfigIncludeAndExclude() throws Exception {
        String[] args = "-D a=b -exclude **/tmp/**".split(" ");
        Config result = App.readConfig(args);
        assertThat(result, is(not(nullValue())));
        assertThat(result.excludes(), is(new String[]{
                "**/tmp/**"
        }));
    }

    @Test
    void testReadConfigIncludeAndExcludeMultiple() throws Exception {
        String[] args = "-D a=b -exclude **/tmp/** -exclude **/0,1,2.zzz".split(" ");
        Config result = App.readConfig(args);
        assertThat(result, is(not(nullValue())));
        assertThat(result.excludes(), is(new String[]{
                "**/tmp/**", "**/0,1,2.zzz"
        }));
    }

}
