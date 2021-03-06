package be.crydust.tokenreplacer;

import java.util.Objects;

/**
 * @author kristof
 */
public final class Strings {

    private Strings() {
    }

    /**
     * A similar method to Objects.requireNonNull.
     *
     * @param string
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public static void requireNonEmpty(String string) {
        Objects.requireNonNull(string);
        if (string.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

}
