package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.dto.scan.CreateFeltStocktakeScanDto;
import ch.portami.inventorybackend.stocktake.felt.dto.scan.FeltStocktakeScanDto;
import ch.portami.inventorybackend.stocktake.felt.service.FeltStocktakeScanService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocktakes/{stocktakeId}/scans")
public class FeltStocktakeScanController {

    private final FeltStocktakeScanService scanService;

    public FeltStocktakeScanController(FeltStocktakeScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping
    public ResponseEntity<FeltStocktakeScanDto> createFeltStocktakeScan(@PathVariable Long stocktakeId,
            @RequestBody @Valid CreateFeltStocktakeScanDto createFeltStocktakeScanDto) {
        return ResponseEntity.ok(scanService.createScan(stocktakeId, createFeltStocktakeScanDto));
    }

    @GetMapping("/{scanId}")
    public ResponseEntity<FeltStocktakeScanDto> getFeltStocktakeScanById(@PathVariable Long stocktakeId,
            @PathVariable Long scanId) {
        return ResponseEntity.ok(scanService.getScan(stocktakeId, scanId));
    }

    @GetMapping()
    public ResponseEntity<List<FeltStocktakeScanDto>> getAllFeltStocktakeScans(@PathVariable Long stocktakeId,
            @RequestParam(required = false) Long storageId) {
        return ResponseEntity.ok(scanService.getScans(stocktakeId, storageId));
    }

    @PostMapping("/{scanId}/void")
    public ResponseEntity<Void> voidFeltStocktakeScan(@PathVariable Long stocktakeId, @PathVariable Long scanId) {
        scanService.voidScan(stocktakeId, scanId);
        return ResponseEntity.noContent().build();
    }

}
