package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.CreateFeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.ExtendStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.FeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.service.FeltStocktakeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Felt Stocktakes", description = "Manage felt stocktakes including creation, extension, completion, and deletion.")
@RestController
@RequestMapping("/api/stocktakes")
public class FeltStocktakeController {

    private final FeltStocktakeService stocktakeService;

    public FeltStocktakeController(FeltStocktakeService stocktakeService) {
        this.stocktakeService = stocktakeService;
    }

    @Operation(summary = "Create a felt stocktake", description = "Creates a new stocktake and initializes items for the selected storages.")
    @ApiResponse(responseCode = "201", description = "Stocktake created and returned in the response body")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "422", description = "One or more referenced storage IDs do not exist")
    @PostMapping
    public ResponseEntity<FeltStocktakeDto> createStocktake(
            @RequestBody @Valid CreateFeltStocktakeDto createFeltStocktakeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(stocktakeService.createStocktake(createFeltStocktakeDto));
    }

    @Operation(summary = "Get a felt stocktake by ID")
    @ApiResponse(responseCode = "200", description = "Stocktake found and returned in the response body")
    @ApiResponse(responseCode = "404", description = "No stocktake exists with the given ID")
    @GetMapping("/{id}")
    public ResponseEntity<FeltStocktakeDto> getStocktakeById(@PathVariable Long id) {
        return ResponseEntity.ok(stocktakeService.getStocktake(id));
    }

    @Operation(summary = "List all felt stocktakes")
    @ApiResponse(responseCode = "200", description = "List of all stocktakes (may be empty)")
    @GetMapping
    public ResponseEntity<List<FeltStocktakeDto>> getAllStocktakes() {
        return ResponseEntity.ok(stocktakeService.getAllStocktakes());
    }

    @Operation(summary = "Delete a felt stocktake", description = "Deletes the stocktake and its associated items, scans, and storage links.")
    @ApiResponse(responseCode = "204", description = "Stocktake successfully deleted or is not existing (anymore)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStocktakeById(@PathVariable Long id) {
        stocktakeService.deleteStocktake(id);
        return ResponseEntity.noContent()
                             .build();
    }

    @Operation(summary = "Extend a felt stocktake", description = "Adds additional storages to an existing stocktake.")
    @ApiResponse(responseCode = "200", description = "Stocktake updated and returned in the response body")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "No stocktake exists with the given ID")
    @ApiResponse(responseCode = "409", description = "Stocktake is already completed and cannot be extended")
    @ApiResponse(responseCode = "422", description = "One or more referenced storage IDs do not exist")
    @PostMapping("/{id}/extend")
    public ResponseEntity<FeltStocktakeDto> extendStocktake(@PathVariable Long id,
            @RequestBody @Valid ExtendStocktakeDto extendStocktakeDto) {
        return ResponseEntity.ok(stocktakeService.extendStocktake(id, extendStocktakeDto));
    }

    @Operation(summary = "Complete a felt stocktake", description = "Finalizes the stocktake after all storages are closed and all issues are resolved.")
    @ApiResponse(responseCode = "200", description = "Stocktake completed and returned in the response body")
    @ApiResponse(responseCode = "404", description = "No stocktake exists with the given ID")
    @ApiResponse(responseCode = "409", description = "Stocktake cannot be completed due to open storages or unresolved issues")
    @PostMapping("/{id}/complete")
    public ResponseEntity<FeltStocktakeDto> finalizeStocktake(@PathVariable Long id) {
        return ResponseEntity.ok(stocktakeService.completeStocktake(id));
    }

}
