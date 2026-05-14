package ch.portami.inventorybackend.stocktake.felt.dto.stocktake;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ExtendStocktakeDto(
        List<@NotNull Long> storageIds
) {

}

