package be.crydust.tokenreplacer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * The business logic of the application. Will search for *.template files. Then
 * will create the resulting file by replacing the tokens within. Existing files
 * are replaced except if a *.readonly file is found. The replaced file is
 * renamed to *.bak.
 */
public class Action implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Action.class);
    // 1 megabyte
    private static final long MAX_SIZE = 1048576;
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    private final Config config;

    /**
     * @param config a valid configuration
     */
    public Action(@Nonnull Config config) {
        Objects.requireNonNull(config);
        this.config = config;
    }

    @Override
    public void run() {
        try {
            TokenReplacer replacer = new TokenReplacer(config.getBegintoken(), config.getEndtoken(), config.getReplacetokens());
            List<Path> templates = new FilesFinder(config.getFolder(), "**/*.template", config.getExcludes()).call();
            for (Path template : templates) {
                Path file = FileExtensionUtil.replaceExtension(template, "");
                if (Files.exists(file)) {
                    if (Files.isDirectory(file)) {
                        System.out.printf("Skipped %s (there is a directory with the same name)%n", file);
                        continue;
                    }
                    Path readonlyFile = FileExtensionUtil.replaceExtension(template, ".readonly");
                    if (Files.exists(readonlyFile)) {
                        System.out.printf("Skipped %s (readonly)%n", file);
                        continue;
                    }
                    Path backupFile = FileExtensionUtil.replaceExtension(template, ".bak");
                    Files.move(file, backupFile, ATOMIC_MOVE, REPLACE_EXISTING);
                }
                if (Files.size(template) > MAX_SIZE) {
                    throw new RuntimeException("file is too large to read");
                }
                String templateContents = Files.readString(template, DEFAULT_ENCODING);
                new FileWriter(replacer.replace(templateContents), file).run();
                System.out.printf("Wrote %s%n", file);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            LOGGER.error(null, ex);
        }
    }

}
