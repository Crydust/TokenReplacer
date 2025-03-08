package be.crydust.tokenreplacer;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class PathHasContent extends TypeSafeDiagnosingMatcher<Path> {

    private final String expectedContent;

    public PathHasContent(String expectedContent) {
        this.expectedContent = expectedContent;
    }

    @Override
    protected boolean matchesSafely(Path actualFile, Description description) {
        String actualString;
        try {
            actualString = Files.readString(actualFile);
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

    public static PathHasContent pathHasContent(String expectedContent) {
        return new PathHasContent(expectedContent);
    }
}
