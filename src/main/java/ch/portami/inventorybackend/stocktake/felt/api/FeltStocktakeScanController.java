package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.dto.CreateFeltStocktakeScanDto;
import ch.portami.inventorybackend.stocktake.felt.dto.FeltStocktakeScanDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/stocktakes/{stocktakeId}/scans")
public class FeltStocktakeScanController {

    @PostMapping
    public ResponseEntity<FeltStocktakeScanDto> createFeltStocktakeScan(@PathVariable Long stocktakeId,
            @RequestBody @Valid CreateFeltStocktakeScanDto createFeltStocktakeScanDto) {
        throw new RuntimeException("Not implemented yet");
    }

    @DeleteMapping("/{scanId}")
    public ResponseEntity<Void> deleteFeltStocktakeScan(@PathVariable Long stocktakeId, @PathVariable Long scanId) {
        throw new RuntimeException("Not implemented yet");
    }

}

