package be.crydust.tokenreplacer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

final class TempDirHelper {

    private TempDirHelper() {
        //NOOP
    }

    static File newFile(Path base, String fileName) throws Exception {
        requireNonNull(base);
        requireNonNull(fileName);
        Path path = base.resolve(fileName);
        if (!path.startsWith(base)) {
            throw new IllegalArgumentException("invalid fileName " + fileName);
        }
        Path parent = path.getParent();
        if (!parent.equals(base)) {
            Files.createDirectories(parent);
        }
        Files.createFile(path);
        return path.toFile();
    }

    static File newFolder(Path base) throws Exception {
        requireNonNull(base);
        return Files.createDirectories(base.resolve(UUID.randomUUID().toString())).toFile();
    }

    static File newFolder(Path base, String folderName) throws Exception {
        requireNonNull(base);
        requireNonNull(folderName);
        final Path path = base.resolve(folderName);
        if (!path.startsWith(base)) {
            throw new IllegalArgumentException("invalid folderName " + folderName);
        }
        return Files.createDirectories(path).toFile();
    }

}
