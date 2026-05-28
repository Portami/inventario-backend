package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.service.FeltStocktakeStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Felt Stocktake Storages", description = "Open and close storages within a felt stocktake.")
@RestController
@RequestMapping("/api/stocktakes/{stocktakeId}/storages")
public class FeltStocktakeStorageController {

    private final FeltStocktakeStorageService storageService;

    public FeltStocktakeStorageController(FeltStocktakeStorageService storageService) {
        this.storageService = storageService;
    }

    @Operation(summary = "Close a stocktake storage", description = "Marks a storage as closed for the stocktake.")
    @ApiResponse(responseCode = "204", description = "Storage closed for the stocktake")
    @ApiResponse(responseCode = "404", description = "No stocktake or storage exists with the given IDs")
    @ApiResponse(responseCode = "409", description = "Stocktake is already completed and cannot be modified")
    @PostMapping("/{storageId}/close")
    public ResponseEntity<Void> closeStocktakeStorage(@PathVariable Long stocktakeId, @PathVariable Long storageId) {
        storageService.closeStorage(stocktakeId, storageId);
        return ResponseEntity.noContent()
                             .build();
    }

    @Operation(summary = "Reopen a stocktake storage", description = "Reopens a storage of a non-completed stocktake.")
    @ApiResponse(responseCode = "204", description = "Storage reopened for the stocktake")
    @ApiResponse(responseCode = "404", description = "No stocktake or storage exists with the given IDs")
    @ApiResponse(responseCode = "409", description = "Stocktake is already completed and cannot be modified")
    @PostMapping("/{storageId}/reopen")
    public ResponseEntity<Void> reopenStocktakeStorage(@PathVariable Long stocktakeId, @PathVariable Long storageId) {
        storageService.reopenStorage(stocktakeId, storageId);
        return ResponseEntity.noContent()
                             .build();
    }

}
