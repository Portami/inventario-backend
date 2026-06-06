package ch.portami.inventorybackend.felt.config;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for felt inventory, bound from {@code portami.felt.*}.
 *
 * @param scrapMinSideCm the minimum side length, in centimetres, a scrap piece must have to be
 *                       worth tracking. During an "Abschneiden" cut, an offcut with any side below
 *                       this value is silently dropped instead of being saved.
 */
@Validated
@ConfigurationProperties(prefix = "portami.felt")
public record FeltProperties(
        @Positive double scrapMinSideCm) {
}
