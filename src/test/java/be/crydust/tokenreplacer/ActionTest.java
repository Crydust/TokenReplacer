package be.crydust.tokenreplacer;

import static be.crydust.tokenreplacer.TempDirHelper.newFile;
import static be.crydust.tokenreplacer.TempDirHelper.newFolder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
        new FileWriter("unchanged", file.toPath()).run();
        new FileWriter("@a@", template.toPath()).run();
        createSimpleAction(folder).run();
        assertThat(new FileReader(file.toPath()).call(), is("A"));
    }

    @Test
    void testReadonly(@TempDir Path folder) throws Exception {
        File file = newFile(folder, "a");
        File template = newFile(folder, "a.template");
        newFile(folder, "a.readonly");
        new FileWriter("unchanged", file.toPath()).run();
        new FileWriter("@a@", template.toPath()).run();
        createSimpleAction(folder).run();
        assertThat(new FileReader(file.toPath()).call(), is("unchanged"));
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
        newFolder(folder, "a");
        newFolder(folder, "a/tmp");
        newFolder(folder, "tmp");
        newFolder(folder, "tmp/a");
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
        new FileWriter("@a@", template1.toPath()).run();
        new FileWriter("@a@", template2.toPath()).run();
        new FileWriter("@a@", template3.toPath()).run();
        new FileWriter("@a@", template4.toPath()).run();
        new FileWriter("@a@", template5.toPath()).run();
        createActionWithExclude(folder, "**/tmp/**").run();
        assertThat(new FileReader(file1.toPath()).call(), is("A"));
        assertThat(new FileReader(file2.toPath()).call(), is(""));
        assertThat(new FileReader(file3.toPath()).call(), is(""));
        assertThat(new FileReader(file4.toPath()).call(), is(""));
        assertThat(new FileReader(file5.toPath()).call(), is("A"));
    }

}
