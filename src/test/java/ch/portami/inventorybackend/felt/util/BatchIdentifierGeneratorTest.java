package ch.portami.inventorybackend.felt.util;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BatchIdentifierGeneratorTest {

    @Test
    void producesUniqueFixedLengthCodesAcrossManyIds() {
        Set<String> codes = new HashSet<>();
        for (long id = 1; id <= 100_000; id++) {
            String code = BatchIdentifierGenerator.createIdentifier(id);
            assertThat(code).hasSize(BatchIdentifierGenerator.IDENTIFIER_LENGTH)
                            .matches("[0-9A-Z]+");
            codes.add(code);
        }
        // Bijection guarantees no duplicates.
        assertThat(codes).hasSize(100_000);
    }

    @Test
    void isDeterministic() {
        assertThat(BatchIdentifierGenerator.createIdentifier(4242L))
                .isEqualTo(BatchIdentifierGenerator.createIdentifier(4242L));
    }

    @Test
    void scramblesConsecutiveIdsSoTheyDoNotLookSequential() {
        String first = BatchIdentifierGenerator.createIdentifier(1L);
        String second = BatchIdentifierGenerator.createIdentifier(2L);
        // Adjacent ids should not yield adjacent-looking codes sharing a long common prefix.
        assertThat(first.charAt(0)).isNotEqualTo(second.charAt(0));
    }
}
