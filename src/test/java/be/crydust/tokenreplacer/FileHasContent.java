package be.crydust.tokenreplacer;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class FileHasContent extends TypeSafeDiagnosingMatcher<File> {

    private final String expectedContent;

    public FileHasContent(String expectedContent) {
        this.expectedContent = expectedContent;
    }

    @Override
    protected boolean matchesSafely(File actualFile, Description description) {
        String actualString;
        try {
            actualString = Files.readString(actualFile.toPath());
        } catch (IOException e) {
            // TODO
            throw new RuntimeException(e);
        }
        description.appendText("was ").appendValue(actualString);
        return Objects.equals(actualString, expectedContent);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("A file containing \"" + expectedContent + "\"");
    }

    public static FileHasContent fileHasContent(String expectedContent) {
        return new FileHasContent(expectedContent);
    }
}
