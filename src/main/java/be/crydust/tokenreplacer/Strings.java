package be.crydust.tokenreplacer;

import java.util.Objects;

public final class Strings {

    private Strings() {
    }

    /**
     * A similar method to Objects.requireNonNull.
     */
    public static void requireNonEmpty(String string) {
        Objects.requireNonNull(string);
        if (string.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

}
