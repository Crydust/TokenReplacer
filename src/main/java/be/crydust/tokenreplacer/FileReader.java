package be.crydust.tokenreplacer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 *
 * @author kristof
 */
public class FileReader implements Callable<String> {

    // 1 megabyte
    private static long MAX_SIZE = 1 * 1024 * 1024;
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    private final Path path;
    private final Charset encoding;

    /**
     * FileReader with default encoding (UTF_8)
     *
     * @param path
     */
    public FileReader(Path path) {
        this(path, DEFAULT_ENCODING);
    }

    /**
     * FileReader with custom encoding
     *
     * @param path
     * @param encoding
     */
    public FileReader(Path path, Charset encoding) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(encoding);
        this.path = path;
        this.encoding = encoding;
    }

    @Override
    public String call() throws Exception {
        if (Files.size(path) > MAX_SIZE) {
            throw new RuntimeException("file is too large to read");
        }
        byte[] encoded = Files.readAllBytes(path);
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

}
