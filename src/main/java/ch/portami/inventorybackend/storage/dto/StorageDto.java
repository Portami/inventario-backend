package ch.portami.inventorybackend.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A storage location.")
public record StorageDto(
        @Schema(description = "Unique identifier of the storage location.")
        Long id,

        @Schema(description = "Name of the storage location.")
        String name
) {

}
