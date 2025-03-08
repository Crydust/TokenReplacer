package be.crydust.tokenreplacer;

import org.hamcrest.FeatureMatcher;

import static org.hamcrest.Matchers.equalTo;

public final class CustomThrowableMatchers {

    public static HasMessage hasMessage(String expected) {
        return new HasMessage(expected);
    }

    public static HasCauseMessage hasCauseMessage(String expected) {
        return new HasCauseMessage(expected);
    }

    private static final class HasMessage extends FeatureMatcher<Throwable, String> {
        public HasMessage(String expectedMessage) {
            super(equalTo(expectedMessage), "message", "message");
        }

        @Override
        protected String featureValueOf(Throwable actual) {
            return actual.getMessage();
        }
    }

    private static final class HasCauseMessage extends FeatureMatcher<Throwable, String> {
        public HasCauseMessage(String expectedMessage) {
            super(equalTo(expectedMessage), "cause message", "cause message");
        }

        @Override
        protected String featureValueOf(Throwable actual) {
            return actual.getCause().getMessage();
        }
    }

}
