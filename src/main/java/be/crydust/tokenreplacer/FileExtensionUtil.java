package be.crydust.tokenreplacer;

import javax.annotation.Nonnull;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * This class consists of {@code static} utility methods for operating on paths.
 * These utilities include a method for replacing the extension of a file.
 */
public final class FileExtensionUtil {

    private FileExtensionUtil() {
    }

    /**
     * Replaces or removes the extension from a filename.
     *
     * <p>
     * <b>Usage Examples:</b>
     * Suppose we want to replace a file extension from "txt" to "csv":
     * <pre>
     *     Path source = Path.of("c:/temp/numbers.txt");
     *     Path destination = FileExtensionUtil.replaceExtension(source, "csv");
     *     // returns c:/temp/numbers.csv
     * </pre>
     *
     * @param path         original path of the file
     * @param newExtension extension for the resulting file. Use an empty string
     *                     to simply remove the extension. Prepending a dot is not necessary.
     * @return path with its filename extension replaced by newExtension
     */
    @Nonnull
    public static Path replaceExtension(@Nonnull Path path, @Nonnull String newExtension) {
        requireNonNull(path);
        requireNonNull(newExtension);
        final Path fileName = path.getFileName();
        if (fileName == null) {
            throw new IllegalArgumentException("fileName is null");
        }
        // replaced by the above to keep spotbugs happy: Objects.requireNonNull(fileName);
        String originalFileName = fileName.toString();
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex == -1
            || dotIndex == originalFileName.length() - 1) {
            throw new IllegalArgumentException("path has no extension");
        }
        final String dot;
        if (newExtension.isEmpty() || newExtension.startsWith(".")) {
            dot = "";
        } else {
            dot = ".";
        }
        String newFileName = originalFileName.substring(0, dotIndex) + dot + newExtension;
        return path.resolveSibling(newFileName);
    }
}
