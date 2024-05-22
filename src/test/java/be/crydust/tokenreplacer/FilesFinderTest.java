package be.crydust.tokenreplacer;

import static be.crydust.tokenreplacer.TempDirHelper.newFile;
import static be.crydust.tokenreplacer.TempDirHelper.newFolder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FilesFinderTest {

    @Test
    void testEmpty(@TempDir Path folder) {
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[0]);
        List<Path> files = cut.call();
        assertThat(files, is(empty()));
    }

    @Test
    void testOneFile(@TempDir Path folder) throws IOException {
        newFile(folder, "a.template");
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[0]);
        List<Path> files = cut.call();
        assertThat(files.size(), is(1));
    }

    @Test
    void testTwoFiles(@TempDir Path folder) throws IOException {
        newFile(folder, "a.template");
        File subFolder = newFolder(folder);
        new File(subFolder, "b.template").createNewFile();
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[0]);
        List<Path> files = cut.call();
        assertThat(files.size(), is(2));
    }

    @Test
    void testExcludeNothing(@TempDir Path folder) throws IOException {
        newFile(folder, "1.template");
        newFolder(folder, "tmp");
        newFile(folder, "tmp/2.template");
        newFolder(folder, "xxx");
        newFile(folder, "xxx/3.template");
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[0]);
        List<Path> files = cut.call();
        assertThat(files.size(), is(3));
    }

    @Test
    void testExcludeOne(@TempDir Path folder) throws IOException {
        newFile(folder, "1.template");
        newFolder(folder, "tmp");
        newFile(folder, "tmp/excluded.template");
        newFolder(folder, "xxx");
        newFile(folder, "xxx/2.template");
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[]{"**/tmp/**"});
        List<Path> files = cut.call();
        assertThat(files.size(), is(2));
    }

    @Test
    void testExcludeTwo(@TempDir Path folder) throws IOException {
        newFile(folder, "1.template");
        newFolder(folder, "tmp");
        newFile(folder, "tmp/excluded.template");
        newFolder(folder, "xxx");
        newFile(folder, "xxx/excluded.template");
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[]{"**/tmp/**", "**/xxx/**"});
        List<Path> files = cut.call();
        assertThat(files.size(), is(1));
    }

    @Test
    void testExcludeEscape(@TempDir Path folder) throws IOException {
        newFile(folder, "1.template");
        newFolder(folder, "tmp");
        newFile(folder, "tmp/excluded.template");
        newFolder(folder, "a[]!{},b");
        newFile(folder, "a[]!{},b/excluded.template");
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[]{"**/tmp/**", "**/a[]!{},b/**"});
        List<Path> files = cut.call();
        assertThat(files.size(), is(2));
    }

}
