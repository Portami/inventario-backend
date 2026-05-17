package ch.portami.inventorybackend.felt.util;

import java.util.Random;

public final class BatchIdentifierGenerator {

    public static final int IDENTIFIER_LENGTH = 8;
    public static final String IDENTIFIER_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private BatchIdentifierGenerator() {}

    public static String createIdentifier(Long id) {
        Random random = new Random(id);
        String chars = IDENTIFIER_CHARACTERS;
        StringBuilder sb = new StringBuilder(IDENTIFIER_LENGTH);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
