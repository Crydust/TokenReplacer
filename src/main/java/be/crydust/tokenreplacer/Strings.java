package be.crydust.tokenreplacer;

import static java.util.Objects.requireNonNull;

public final class Strings {

    private Strings() {
    }

    /**
     * A similar method to Objects.requireNonNull.
     */
    public static void requireNonEmpty(String string) {
        requireNonNull(string);
        if (string.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

}
