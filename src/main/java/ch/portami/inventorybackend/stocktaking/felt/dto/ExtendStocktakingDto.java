package ch.portami.inventorybackend.stocktaking.felt.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ExtendStocktakingDto(
        List<@NotNull Long> storageIds
) {

}
