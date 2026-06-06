package ch.portami.inventorybackend.felt.util;

import java.util.Locale;

/**
 * Utility class for generating batch identifiers from a numeric ID.
 *
 * <p>The identifier is the ID encoded in base&nbsp;36 (digits {@code 0-9} and letters {@code A-Z}),
 * left-padded with zeros to {@value #IDENTIFIER_LENGTH} characters. Because the encoding is a
 * one-to-one mapping of the ID, distinct (unique) IDs always produce distinct identifiers — there is
 * no collision risk as long as the supplied ID is unique. The mapping is also deterministic: the same
 * ID always yields the same identifier.
 */
public final class BatchIdentifierGenerator {

    /**
     * The minimum length of a generated identifier; shorter encodings are left-padded with zeros.
     * IDs whose base-36 encoding is longer (beyond ~2.8e12) simply produce a longer string.
     */
    public static final int IDENTIFIER_LENGTH = 8;

    private static final int BASE36 = 36;

    private BatchIdentifierGenerator() {
    }

    /**
     * Creates a batch identifier from the given ID by encoding it in base 36 and left-padding it to
     * {@value #IDENTIFIER_LENGTH} characters.
     *
     * @param id the unique ID to encode (must be non-negative)
     * @return the base-36 identifier, at least {@value #IDENTIFIER_LENGTH} characters long
     */
    public static String createIdentifier(Long id) {
        String encoded = Long.toString(id, BASE36)
                             .toUpperCase(Locale.ROOT);
        if (encoded.length() >= IDENTIFIER_LENGTH) {
            return encoded;
        }
        return "0".repeat(IDENTIFIER_LENGTH - encoded.length()) + encoded;
    }
}
