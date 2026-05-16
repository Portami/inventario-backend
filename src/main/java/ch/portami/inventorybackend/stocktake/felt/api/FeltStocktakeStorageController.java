package ch.portami.inventorybackend.stocktake.felt.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocktakes/{stocktakeId}/storages")
public class FeltStocktakeStorageController {

    @PostMapping("/{storageId}/close")
    public void closeStorageStocktake(@PathVariable Long stocktakeId, @PathVariable Long storageId) {
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/{storageId}/reopen")
    public void reopenStorageStocktake(@PathVariable Long stocktakeId, @PathVariable Long storageId) {
        throw new RuntimeException("Not implemented yet");
    }

}
