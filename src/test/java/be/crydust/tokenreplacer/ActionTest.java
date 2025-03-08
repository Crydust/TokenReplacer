package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static be.crydust.tokenreplacer.ActionBuilder.anAction;
import static be.crydust.tokenreplacer.FileHasContent.fileHasContent;
import static be.crydust.tokenreplacer.PathHasContent.pathHasContent;
import static be.crydust.tokenreplacer.TempDirHelper.newFile;
import static be.crydust.tokenreplacer.TempDirHelper.newFolder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.hamcrest.io.FileMatchers.aFileWithSize;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.jupiter.api.Assertions.assertAll;

class ActionTest {

    private static Action createSimpleAction(Path folder) {
        return anAction(folder)
                .withBeginToken("@")
                .withEndToken("@")
                .withReplaceTokens(Map.of("a", "A"))
                .withQuiet(true)
                .build();
    }

    private static Action createActionWithExclude(Path folder, String exclude) {
        return anAction(folder)
                .withBeginToken("@")
                .withEndToken("@")
                .withReplaceTokens(Map.of("a", "A"))
                .withQuiet(true)
                .withExcludes(exclude)
                .build();
    }

    private static Action createActionWithOut(Path folder, PrintStream out) {
        return anAction(folder)
                .withOut(out)
                .build();
    }

    @Test
    void normal(@TempDir Path folder) throws Exception {
        File file = newFile(folder, "a");
        File template = newFile(folder, "a.template");
        Files.writeString(file.toPath(), "unchanged");
        Files.writeString(template.toPath(), "@a@");

        createSimpleAction(folder).run();

        assertThat(file, fileHasContent("A"));
    }

    @Test
    void readonly(@TempDir Path folder) throws Exception {
        File file = newFile(folder, "a");
        File template = newFile(folder, "a.template");
        newFile(folder, "a.readonly");
        Files.writeString(file.toPath(), "unchanged");
        Files.writeString(template.toPath(), "@a@");

        createSimpleAction(folder).run();

        assertThat(file, fileHasContent("unchanged"));
    }

    @Test
    void folderIntheWay(@TempDir Path folder) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newFolder(folder, "a");
        newFile(folder, "a.template");

        createActionWithOut(folder, new PrintStream(baos)).run();

        assertThat(baos.toString(), stringContainsInOrder("Skipped ", " (there is a directory with the same name)"));
    }

    @Test
    void exclude(@TempDir Path folder) throws Exception {
        File file1 = newFile(folder, "1");
        File template1 = newFile(folder, "1.template");
        File file2 = newFile(folder, "tmp/2");
        File template2 = newFile(folder, "tmp/2.template");
        File file3 = newFile(folder, "a/tmp/3");
        File template3 = newFile(folder, "a/tmp/3.template");
        File file4 = newFile(folder, "tmp/a/4");
        File template4 = newFile(folder, "tmp/a/4.template");
        File file5 = newFile(folder, "a/5");
        File template5 = newFile(folder, "a/5.template");
        Files.writeString(template1.toPath(), "@a@");
        Files.writeString(template2.toPath(), "@a@");
        Files.writeString(template3.toPath(), "@a@");
        Files.writeString(template4.toPath(), "@a@");
        Files.writeString(template5.toPath(), "@a@");

        createActionWithExclude(folder, "**/tmp/**").run();

        assertAll(
                () -> assertThat(file1, fileHasContent("A")),
                () -> assertThat(file2, aFileWithSize(0)),
                () -> assertThat(file3, aFileWithSize(0)),
                () -> assertThat(file4, aFileWithSize(0)),
                () -> assertThat(file5, fileHasContent("A"))
        );
    }

    @Test
    void templateTooLarge(@TempDir Path folder) throws Exception {
        File file = newFile(folder, "a");
        Files.writeString(file.toPath(), "original");
        File template = newFile(folder, "a.template");
        Files.writeString(template.toPath(), "c".repeat(1048576 + 1));

        createSimpleAction(folder).run();

        assertAll(
                () -> assertThat(file, not(anExistingFile())),
                () -> assertThat(folder.resolve("a.bak"), pathHasContent("original")),
                () -> assertThat(folder.resolve("a").toFile(), not(anExistingFile())),
                () -> assertThat(folder.resolve("a.bak").toFile(), anExistingFile()),
                () -> assertThat(folder.resolve("a.template").toFile(), anExistingFile())
        );
    }

}
