package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.service.FeltStocktakeStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocktakes/{stocktakeId}/storages")
public class FeltStocktakeStorageController {

    private final FeltStocktakeStorageService storageService;

    public FeltStocktakeStorageController(FeltStocktakeStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/{storageId}/close")
    public ResponseEntity<Void> closeStocktakeStorage(@PathVariable Long stocktakeId, @PathVariable Long storageId) {
        storageService.closeStorage(stocktakeId, storageId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{storageId}/reopen")
    public ResponseEntity<Void> reopenStocktakeStorage(@PathVariable Long stocktakeId, @PathVariable Long storageId) {
        storageService.reopenStorage(stocktakeId, storageId);
        return ResponseEntity.noContent().build();
    }

}
