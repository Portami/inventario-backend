package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A batch, is the representation of specific attributes in the creation of a felt roll")
public record BatchDto(
        @Schema(description = "ID of the delivery batch") Long id,

        @Schema(description = "Name of the delivery batch") String name) {

}
