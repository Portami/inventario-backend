package ch.portami.inventorybackend.felt.util;

import java.math.BigInteger;
import java.util.Locale;

/**
 * Generates random-looking, fixed-length batch identifiers from a numeric ID.
 *
 * <p>The ID is run through an affine bijection over the 8-character base-36 code space
 * ({@code 36^8}) and the result is base-36 encoded (digits {@code 0-9} and letters {@code A-Z}),
 * left-padded to {@value #IDENTIFIER_LENGTH} characters. Because the mapping is a
 * <em>bijection</em>, distinct IDs are guaranteed to produce distinct codes — there are no
 * collisions for the realistic ID range — while the golden-ratio ("Fibonacci hashing") multiplier
 * scatters consecutive IDs across the whole space so the codes look random (e.g. {@code R7K9M2PL})
 * rather than sequential. The mapping is also deterministic: the same ID always yields the same code.
 */
public final class BatchIdentifierGenerator {

    /** The length of each generated identifier. */
    public static final int IDENTIFIER_LENGTH = 8;

    private static final int BASE36 = 36;

    /** Size of the code space: {@code 36^8 = 2^16 * 3^16}. */
    private static final BigInteger SPACE = BigInteger.valueOf(BASE36).pow(IDENTIFIER_LENGTH);

    /**
     * Multiplier for the affine scramble: {@code round(SPACE / golden_ratio)}. It is odd and not
     * divisible by 3, hence coprime to {@code SPACE = 2^16 * 3^16}, which makes the mapping a
     * bijection (the precondition for zero collisions). Its size near {@code 0.618 * SPACE} makes
     * consecutive IDs wrap around the space, scattering the codes.
     */
    private static final BigInteger MULTIPLIER = BigInteger.valueOf(1_743_541_810_475L);

    /** Arbitrary additive offset so that low IDs do not start near all-zero codes. */
    private static final BigInteger OFFSET = BigInteger.valueOf(982_451_653L);

    private BatchIdentifierGenerator() {
    }

    /**
     * Creates a batch identifier from the given ID.
     *
     * @param id the unique, non-negative ID to encode
     * @return a {@value #IDENTIFIER_LENGTH}-character code over {@code [0-9A-Z]}, unique per ID
     */
    public static String createIdentifier(Long id) {
        long scrambled = BigInteger.valueOf(id)
                                   .multiply(MULTIPLIER)
                                   .add(OFFSET)
                                   .mod(SPACE)
                                   .longValueExact();
        String encoded = Long.toString(scrambled, BASE36)
                             .toUpperCase(Locale.ROOT);
        return "0".repeat(IDENTIFIER_LENGTH - encoded.length()) + encoded;
    }
}
