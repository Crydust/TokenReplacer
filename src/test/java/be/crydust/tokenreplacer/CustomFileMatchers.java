package be.crydust.tokenreplacer;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.equalTo;

public final class CustomFileMatchers {

    private CustomFileMatchers() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Matcher<File> fileHasContent(String expectedContent) {
        return new FeatureMatcher<>(equalTo(expectedContent), "a file containing", "content") {
            @Override
            protected String featureValueOf(File actualFile) {
                return content(actualFile.toPath());
            }
        };
    }

    public static Matcher<Path> pathHasContent(String expectedContent) {
        return new FeatureMatcher<>(equalTo(expectedContent), "a file containing", "content") {
            @Override
            protected String featureValueOf(Path actualFilePath) {
                return content(actualFilePath);
            }
        };
    }

    private static String content(Path actualFilePath) {
        try {
            return Files.readString(actualFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
