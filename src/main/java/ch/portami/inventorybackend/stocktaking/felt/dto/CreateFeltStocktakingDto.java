package ch.portami.inventorybackend.stocktaking.felt.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateFeltStocktakingDto(
        @NotNull String description,
        List<@NotNull Long> storageIds
) {

}
