package be.crydust.tokenreplacer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kristof
 */
public class FileWriter implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileWriter.class);

    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    private final String contents;
    private final Path path;
    private final Charset encoding;

    /**
     * FileWriter with default encoding (UTF_8)
     *
     * @param contents
     * @param path
     */
    public FileWriter(String contents, Path path) {
        this(contents, path, DEFAULT_ENCODING);
    }

    /**
     * FileWriter with custom encoding
     *
     * @param contents
     * @param path
     * @param encoding
     */
    public FileWriter(String contents, Path path, Charset encoding) {
        Objects.requireNonNull(contents);
        Objects.requireNonNull(path);
        Objects.requireNonNull(encoding);
        this.contents = contents;
        this.path = path;
        this.encoding = encoding;
    }

    @Override
    public void run() {
        //Writing to file
        try {
            Files.write(path, contents.getBytes(encoding));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            LOGGER.error("FileWriter failed");
            throw new RuntimeException(ex);
        }
    }

}
