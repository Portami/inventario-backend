package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.dto.scan.CreateFeltStocktakeScanDto;
import ch.portami.inventorybackend.stocktake.felt.dto.scan.FeltStocktakeScanDto;
import ch.portami.inventorybackend.stocktake.felt.service.FeltStocktakeScanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Felt Stocktake Scans", description = "Record and manage scans captured during felt stocktakes.")
@RestController
@RequestMapping("/api/stocktakes/{stocktakeId}/scans")
public class FeltStocktakeScanController {

    private final FeltStocktakeScanService scanService;

    public FeltStocktakeScanController(FeltStocktakeScanService scanService) {
        this.scanService = scanService;
    }

    @Operation(summary = "Create a stocktake scan", description = "Records a scan for a felt roll or scrap piece within a stocktake.")
    @ApiResponse(responseCode = "201", description = "Scan created and returned in the response body")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "No stocktake exists with the given ID")
    @ApiResponse(responseCode = "409", description = "Stocktake is already completed and cannot be modified")
    @ApiResponse(responseCode = "422", description = "The request is referencing a storage that does not exists or is not part of this stocktake")
    @PostMapping
    public ResponseEntity<FeltStocktakeScanDto> createFeltStocktakeScan(@PathVariable Long stocktakeId,
            @RequestBody @Valid CreateFeltStocktakeScanDto createFeltStocktakeScanDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(scanService.createScan(stocktakeId, createFeltStocktakeScanDto));
    }

    @Operation(summary = "Get a stocktake scan by ID")
    @ApiResponse(responseCode = "200", description = "Scan found and returned in the response body")
    @ApiResponse(responseCode = "404", description = "No stocktake or scan exists with the given IDs")
    @GetMapping("/{scanId}")
    public ResponseEntity<FeltStocktakeScanDto> getFeltStocktakeScanById(@PathVariable Long stocktakeId,
            @PathVariable Long scanId) {
        return ResponseEntity.ok(scanService.getScan(stocktakeId, scanId));
    }

    @Operation(summary = "List stocktake scans", description = "Returns all scans for a stocktake, optionally filtered by storage.")
    @ApiResponse(responseCode = "200", description = "List of scans returned in the response body")
    @ApiResponse(responseCode = "404", description = "No stocktake exists with the given ID")
    @GetMapping()
    public ResponseEntity<List<FeltStocktakeScanDto>> getAllFeltStocktakeScans(@PathVariable Long stocktakeId,
            @RequestParam(required = false) Long storageId) {
        return ResponseEntity.ok(scanService.getScans(stocktakeId, storageId));
    }

    @Operation(summary = "Void a stocktake scan", description = "Marks a scan as voided if it has not been locked by a resolved problem.")
    @ApiResponse(responseCode = "204", description = "Scan successfully voided")
    @ApiResponse(responseCode = "404", description = "No stocktake or scan exists with the given IDs")
    @ApiResponse(responseCode = "409", description = "Scan is locked or stocktake is completed")
    @PostMapping("/{scanId}/void")
    public ResponseEntity<Void> voidFeltStocktakeScan(@PathVariable Long stocktakeId, @PathVariable Long scanId) {
        scanService.voidScan(stocktakeId, scanId);
        return ResponseEntity.noContent()
                             .build();
    }

}
