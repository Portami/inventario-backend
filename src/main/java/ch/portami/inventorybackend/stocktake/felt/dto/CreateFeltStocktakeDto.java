package ch.portami.inventorybackend.stocktake.felt.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateFeltStocktakeDto(
        @NotNull String description,
        List<@NotNull Long> storageIds
) {

}

