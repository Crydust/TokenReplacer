package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static be.crydust.tokenreplacer.TempDirHelper.newFile;
import static be.crydust.tokenreplacer.TempDirHelper.newFolder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

class FilesFinderTest {

    @Test
    void noFile(@TempDir Path folder) {
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[0]);
        List<Path> files = cut.get();
        assertThat(files, empty());
    }

    @Test
    void oneFile(@TempDir Path folder) throws Exception {
        newFile(folder, "a.template");
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[0]);
        List<Path> files = cut.get();
        assertThat(files, containsInAnyOrder(folder.resolve("a.template")));
    }

    @Test
    void twoFiles(@TempDir Path folder) throws Exception {
        newFile(folder, "a.template");
        File subFolder = newFolder(folder);
        new File(subFolder, "b.template").createNewFile();
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[0]);
        List<Path> files = cut.get();
        assertThat(files, containsInAnyOrder(
                folder.resolve("a.template"),
                folder.resolve(subFolder.toPath().resolve("b.template"))));
    }

    @Test
    void excludeNothing(@TempDir Path folder) throws Exception {
        newFile(folder, "1.template");
        newFile(folder, "tmp/2.template");
        newFile(folder, "xxx/3.template");
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[0]);
        List<Path> files = cut.get();
        assertThat(files, containsInAnyOrder(
                folder.resolve("1.template"),
                folder.resolve("tmp/2.template"),
                folder.resolve("xxx/3.template")
        ));
    }

    @Test
    void excludeOne(@TempDir Path folder) throws Exception {
        newFile(folder, "1.template");
        newFile(folder, "tmp/excluded.template");
        newFile(folder, "xxx/2.template");
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[]{"**/tmp/**"});
        List<Path> files = cut.get();
        assertThat(files, containsInAnyOrder(
                folder.resolve("1.template"),
                folder.resolve("xxx/2.template")
        ));
    }

    @Test
    void excludeTwo(@TempDir Path folder) throws Exception {
        newFile(folder, "1.template");
        newFile(folder, "tmp/excluded.template");
        newFile(folder, "xxx/excluded.template");
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[]{"**/tmp/**", "**/xxx/**"});
        List<Path> files = cut.get();
        assertThat(files, containsInAnyOrder(folder.resolve("1.template")));
    }

    @Test
    void excludeEscape(@TempDir Path folder) throws Exception {
        newFile(folder, "1.template");
        newFile(folder, "tmp/excluded.template");
        newFile(folder, "a[]!{},b/excluded.template");
        newFile(folder, "comma,comma/excluded.template");
        FilesFinder cut = new FilesFinder(folder, "**/*.template", new String[]{"**/tmp/**", "**/a[]!{},b/**", "**/comma,comma/**"});
        List<Path> files = cut.get();
        assertThat(files, containsInAnyOrder(folder.resolve("1.template")));
    }

    @Test
    void shouldFindFileWithComma(@TempDir Path folder) throws Exception {
        newFile(folder, "comma,comma.template");
        FilesFinder cut = new FilesFinder(folder, "**/comma,comma.template", new String[0]);
        List<Path> files = cut.get();
        assertThat(files, containsInAnyOrder(folder.resolve("comma,comma.template")));
    }

}
