package be.crydust.tokenreplacer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class FilesFinderPatternsToGlobTest {

    @Test
    void shouldConvertSimplePattern() {
        String[] patterns = {"a"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob, is("glob:a"));
    }

    @Test
    void shouldConvertMultipleSimplePatterns() {
        String[] patterns = {"a", "b"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob, is("glob:{a,b}"));
    }

    @Test
    void shouldConvertPatternsWithQuestionMark() {
        String[] patterns = {"a?"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob, is("glob:a?"));
    }

    @Test
    void shouldConvertPatternsWithStar() {
        String[] patterns = {"a*"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob, is("glob:a*"));
    }

    @Test
    void shouldConvertPatternsWithDoubleStarAtStart() {
        String[] patterns = {"**/a"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob, is("glob:{a,**/a}"));
    }

    @Test
    void shouldConvertPatternsWithDoubleStarAtEnd() {
        String[] patterns = {"a/**"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob, is("glob:a/**"));
    }
    @Test
    void shouldConvertMultipleSimplePatternsWithCommas() {
        String[] patterns = {"a", "b", "c,d"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob, is("glob:{a,b,c[,]d}"));
    }

}
