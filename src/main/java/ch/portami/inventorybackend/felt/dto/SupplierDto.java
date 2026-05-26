package ch.portami.inventorybackend.felt.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A supplier that provides felt materials.")
public record SupplierDto(
        @Schema(description = "Supplier ID.") Long id,

        @Schema(description = "Supplier name.") String name) {

}
