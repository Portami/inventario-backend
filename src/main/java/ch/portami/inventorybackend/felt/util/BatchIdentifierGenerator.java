package ch.portami.inventorybackend.felt.util;

import java.util.Random;
import java.util.stream.Collectors;

/**
 * Utility class for generating deterministic batch identifiers.
 * <p>
 * Each identifier is a fixed-length alphanumeric string derived from a
 * numeric ID. The generation is deterministic: the same input ID will
 * always produce the same identifier.
 */
public final class BatchIdentifierGenerator {

    /** The length of each generated identifier. */
    public static final int IDENTIFIER_LENGTH = 8;

    /** The character pool used to compose identifiers. */
    public static final String IDENTIFIER_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private BatchIdentifierGenerator() {}

    /**
     * Creates a deterministic identifier from the given ID.
     * <p>
     * The same input ID will always produce the same identifier,
     * as the ID is used to seed the random number generator.
     *
     * @param id the seed used to generate the identifier
     * @return a string of length {@value #IDENTIFIER_LENGTH} composed
     *         of characters from {@link #IDENTIFIER_CHARACTERS}
     */
    public static String createIdentifier(Long id) {
        return new Random(id).ints(IDENTIFIER_LENGTH, 0, IDENTIFIER_CHARACTERS.length())
                     .mapToObj(i -> String.valueOf(IDENTIFIER_CHARACTERS.charAt(i)))
                     .collect(Collectors.joining());
    }
}
