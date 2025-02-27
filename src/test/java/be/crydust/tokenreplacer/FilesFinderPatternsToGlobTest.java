package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FilesFinderPatternsToGlobTest {

    @Test
    void shouldConvertSimplePattern() {
        String[] patterns = {"a"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob).isIn("glob:a", "glob:{a}");
    }

    @Test
    void shouldConvertMultipleSimplePatterns() {
        String[] patterns = {"a", "b"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob).isIn("glob:{a,b}");
    }

    @Test
    void shouldConvertPatternsWithQuestionMark() {
        String[] patterns = {"a?"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob).isIn("glob:a?", "glob:{a?}");
    }

    @Test
    void shouldConvertPatternsWithStar() {
        String[] patterns = {"a*"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob).isIn("glob:a*", "glob:{a*}");
    }

    @Test
    void shouldConvertPatternsWithDoubleStarAtStart() {
        String[] patterns = {"**/a"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob).isIn("glob:{a,**/a}");
    }

    @Test
    void shouldConvertPatternsWithDoubleStarAtEnd() {
        String[] patterns = {"a/**"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob).isIn("glob:a/**", "glob:{a/**}");
    }

    @Test
    void shouldConvertMultipleSimplePatternsWithCommas() {
        String[] patterns = {"a", "b", "c,d"};
        String glob = FilesFinder.patternsToGlob(patterns);
        assertThat(glob).isIn("glob:{a,b,c[,]d}");
    }

}
