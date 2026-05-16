package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.dto.scan.CreateFeltStocktakeScanDto;
import ch.portami.inventorybackend.stocktake.felt.dto.scan.FeltStocktakeScanDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/stocktakes/{stocktakeId}/scans")
public class FeltStocktakeScanController {

    @PostMapping
    public ResponseEntity<FeltStocktakeScanDto> createFeltStocktakeScan(@PathVariable Long stocktakeId,
            @RequestBody @Valid CreateFeltStocktakeScanDto createFeltStocktakeScanDto) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/{scanId}")
    public ResponseEntity<FeltStocktakeScanDto> getFeltStocktakeScanById(@PathVariable Long stocktakeId,
            @PathVariable Long scanId) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping()
    public ResponseEntity<List<FeltStocktakeScanDto>> getAllFeltStocktakeScans(@PathVariable Long stocktakeId,
            @RequestParam(required = false) Long storageId) {
        throw new RuntimeException("Not implemented yet");
    }

    @DeleteMapping("/{scanId}")
    public ResponseEntity<Void> deleteFeltStocktakeScan(@PathVariable Long stocktakeId, @PathVariable Long scanId) {
        throw new RuntimeException("Not implemented yet");
    }

}

