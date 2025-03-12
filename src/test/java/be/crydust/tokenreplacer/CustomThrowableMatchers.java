package be.crydust.tokenreplacer;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.equalTo;

public final class CustomThrowableMatchers {

    private CustomThrowableMatchers() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Matcher<Throwable> hasMessage(String expectedMessage) {
        return new FeatureMatcher<>(equalTo(expectedMessage), "an exception with message", "message") {
            @Override
            protected String featureValueOf(Throwable actual) {
                return actual.getMessage();
            }
        };
    }

    public static Matcher<Throwable> hasCauseMessage(String expectedMessage) {
        return new FeatureMatcher<>(equalTo(expectedMessage), "an exception with cause message", "cause message") {
            @Override
            protected String featureValueOf(Throwable actual) {
                return actual.getCause().getMessage();
            }
        };
    }
}
