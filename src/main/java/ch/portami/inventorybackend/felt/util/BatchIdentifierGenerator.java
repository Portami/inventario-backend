package ch.portami.inventorybackend.felt.util;

import java.util.Random;

public final class BatchIdentifierGenerator {

    private BatchIdentifierGenerator() {}

    public static String createIdentifier(Long id) {
        Random random = new Random(id);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
