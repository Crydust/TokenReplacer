package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static be.crydust.tokenreplacer.TempDirHelper.newFile;
import static be.crydust.tokenreplacer.TempDirHelper.newFolder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ActionTest {

    private static Action createSimpleAction(Path folder) {
        String begintoken = "@";
        String endtoken = "@";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("a", "A");
        boolean quiet = true;
        Config config = new Config(begintoken, endtoken, replacetokens, folder, quiet, new String[0]);
        return new Action(config);
    }

    private static Action createActionWithExclude(Path folder, String exclude) {
        String begintoken = "@";
        String endtoken = "@";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("a", "A");
        boolean quiet = true;
        Config config = new Config(begintoken, endtoken, replacetokens, folder, quiet, new String[]{exclude});
        return new Action(config);
    }

    @Test
    void testNormal(@TempDir Path folder) throws Exception {
        File file = newFile(folder, "a");
        File template = newFile(folder, "a.template");
        Files.writeString(file.toPath(), "unchanged");
        Files.writeString(template.toPath(), "@a@");
        createSimpleAction(folder).run();
        assertThat(Files.readString(file.toPath()), is("A"));
    }

    @Test
    void testReadonly(@TempDir Path folder) throws Exception {
        File file = newFile(folder, "a");
        File template = newFile(folder, "a.template");
        newFile(folder, "a.readonly");
        Files.writeString(file.toPath(), "unchanged");
        Files.writeString(template.toPath(), "@a@");
        createSimpleAction(folder).run();
        assertThat(Files.readString(file.toPath()), is("unchanged"));
    }

    @Test
    void testFolderIntheWay(@TempDir Path folder) throws Exception {
        Action cut = createSimpleAction(folder);
        newFolder(folder, "a");
        newFile(folder, "a.template");
        cut.run();
    }

    @Test
    void testExclude(@TempDir Path folder) throws Exception {
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
        assertThat(Files.readString(file1.toPath()), is("A"));
        assertThat(Files.readString(file2.toPath()), is(""));
        assertThat(Files.readString(file3.toPath()), is(""));
        assertThat(Files.readString(file4.toPath()), is(""));
        assertThat(Files.readString(file5.toPath()), is("A"));
    }

    @Test
    void testTemplateTooLarge(@TempDir Path folder) throws Exception {
        File file = newFile(folder, "a");
        File template = newFile(folder, "a.template");
        Files.writeString(template.toPath(), "c".repeat(1048576 + 1));
        Action action = createSimpleAction(folder);

        action.run();

        assertThat(Files.exists(file.toPath()), is(false));
    }

}
