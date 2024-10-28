package be.crydust.tokenreplacer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class FilesFinder implements Supplier<List<Path>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesFinder.class);

    private final Path path;
    private final PathMatcher includesMatcher;
    private final PathMatcher excludesMatcher;

    private static final PathMatcher ALL_FALSE = it -> false;

    /**
     * FilesFinder with only one include pattern
     */
    public FilesFinder(Path path, String include, String[] excludes) {
        this(path, new String[]{include}, excludes);
    }

    /**
     * FilesFinder with only one multiple include patterns
     */
    private FilesFinder(Path path, String[] includes, String[] excludes) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(includes);
        Objects.requireNonNull(excludes);
        if (includes.length == 0) {
            throw new IllegalArgumentException("includes should not be empty");
        }
        this.path = path;
        this.includesMatcher = FileSystems.getDefault()
                .getPathMatcher(patternsToGlob(includes));
        if (excludes.length == 0) {
            this.excludesMatcher = ALL_FALSE;
        } else {
            this.excludesMatcher = FileSystems.getDefault()
                    .getPathMatcher(patternsToGlob(excludes));
        }
    }

    @Override
    public List<Path> get() {
        try (Stream<Path> stream = Files.find(path, Integer.MAX_VALUE, (file, attrs) -> {
            Path relativePath = path.relativize(file);
            return includesMatcher.matches(relativePath)
                    && !excludesMatcher.matches(relativePath);
        })) {
            return stream.toList();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            LOGGER.error(null, ex);
        }
        return List.of();
    }

    private static String escapeGlob(String pattern) {
        return pattern.replaceAll("[\\[\\]{}]", "\\\\$0")
                .replace(",", "[,]");
    }

    /* package-private for test */
    static String patternsToGlob(String[] patterns) {
        return Arrays.stream(patterns)
                .flatMap(pattern -> pattern.startsWith("**/")
                        ? Stream.of(pattern.substring(3), pattern)
                        : Stream.of(pattern))
                .map(FilesFinder::escapeGlob)
                .collect(joining(",", "glob:{", "}"));
    }

}
