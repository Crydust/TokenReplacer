package be.crydust.tokenreplacer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.crydust.tokenreplacer.TempDirHelper.newFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class IntegrationTest {

    @Test
    void testWriteAndRead(@TempDir Path folder) throws Exception {
        String input = "Lorem ipsum";
        Path a = newFile(folder, "a").toPath();
        new FileWriter(input, a).run();
        String output = Files.readString(a);
        assertThat(output, is(input));
    }

    @Test
    void testReplaceWriteAndRead(@TempDir Path folder) throws Exception {
        String expected = "Lorem ipsum";
        String begintoken = "@";
        String endtoken = "@";
        String input = "@a@ @b@";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("a", "Lorem");
        replacetokens.put("b", "ipsum");
        TokenReplacer replacer = new TokenReplacer(begintoken, endtoken, replacetokens);
        String replaced = replacer.replace(input);
        Path a = newFile(folder, "a").toPath();
        new FileWriter(replaced, a).run();
        String output = Files.readString(a);
        assertThat(output, is(expected));
    }

    @Test
    void testFindReadReplaceWriteAndRead(@TempDir Path folder) throws Exception {
        String expected = "Lorem ipsum";
        String begintoken = "@";
        String endtoken = "@";
        String input = "@a@ @b@";
        Map<String, String> replacetokens = new HashMap<>();
        replacetokens.put("a", "Lorem");
        replacetokens.put("b", "ipsum");
        File aTemplate = newFile(folder, "a.template");

        new FileWriter(input, aTemplate.toPath()).run();
        TokenReplacer replacer = new TokenReplacer(begintoken, endtoken, replacetokens);
        List<Path> templates = new FilesFinder(folder, "**/*.template", new String[0]).call();
        for (Path template : templates) {
            Path file = FileExtensionUtil.replaceExtension(template, "");
            if (Files.exists(file)) {
                String fileContents = Files.readString(file);
                Path backupFile = FileExtensionUtil.replaceExtension(template, ".bak");
                new FileWriter(fileContents, backupFile).run();
            }
            String templateContents = Files.readString(template);
            new FileWriter(replacer.replace(templateContents), file).run();
        }
        String output = Files.readString(folder.resolve("a"));

        assertThat(output, is(expected));
    }

}
