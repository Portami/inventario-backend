package ch.portami.inventorybackend.cutassistant.config;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the cutting assistant, bound from {@code portami.cutting.*}.
 *
 * @param marginCm the cutting margin, in centimetres, added to every edge of each required piece to
 *                 allow for edge-wrapping and fixing
 */
@Validated
@ConfigurationProperties(prefix = "portami.cutting")
public record CuttingProperties(
        @Positive double marginCm) {
}
