package be.crydust.tokenreplacer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * The business logic of the application. Will search for *.template files. Then
 * will create the resulting file by replacing the tokens within. Existing files
 * are replaced except if a *.readonly file is found. The replaced file is
 * renamed to *.bak.
 */
public final class Action implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Action.class);
    // 1 megabyte
    private static final long MAX_SIZE = 1048576;
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    private final TokenReplacer replacer;
    private final FilesFinder filesFinder;

    /**
     * @param config a valid configuration
     */
    public Action(@Nonnull Config config) {
        Objects.requireNonNull(config);
        replacer = new TokenReplacer(config.begintoken(), config.endtoken(), config.replacetokens());
        filesFinder = new FilesFinder(config.folder(), "**/*.template", config.excludes());
    }

    @Override
    public void run() {
        try {
            List<Path> templates = filesFinder.get();
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
                Files.writeString(file, replacer.replace(templateContents), DEFAULT_ENCODING);
                System.out.printf("Wrote %s%n", file);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            LOGGER.error(null, ex);
        }
    }

}
