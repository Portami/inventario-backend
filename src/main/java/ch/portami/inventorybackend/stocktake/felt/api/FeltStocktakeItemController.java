package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemDto;
import ch.portami.inventorybackend.stocktake.felt.dto.item.ResolveFeltStocktakeProblemDto;
import ch.portami.inventorybackend.stocktake.felt.service.FeltStocktakeItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
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

@Tag(name = "Felt Stocktake Items", description = "Inspect stocktake items and resolve stocktake problems.")
@RestController
@RequestMapping("/api/stocktakes/{stocktakeId}/items")
public class FeltStocktakeItemController {

    private final FeltStocktakeItemService itemService;

    public FeltStocktakeItemController(FeltStocktakeItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(summary = "List stocktake items", description = "Returns all stocktake items, optionally filtered by storage. If a storage is specified, all items that are either expected or actually found in this storage will be returned.")
    @ApiResponse(responseCode = "200", description = "Items returned in the response body")
    @ApiResponse(responseCode = "404", description = "No stocktake exists with the given ID")
    @GetMapping
    public ResponseEntity<List<FeltStocktakeItemDto>> getStocktakeItems(@PathVariable Long stocktakeId,
            @RequestParam(required = false) @Nullable Long storageId) {
        return ResponseEntity.ok(itemService.getItems(stocktakeId, storageId));
    }

    @Operation(summary = "Get a stocktake item by ID")
    @ApiResponse(responseCode = "200", description = "Item found and returned in the response body")
    @ApiResponse(responseCode = "404", description = "No stocktake or item exists with the given IDs")
    @GetMapping("/{itemId}")
    public ResponseEntity<FeltStocktakeItemDto> getStocktakeRollById(@PathVariable Long stocktakeId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItem(stocktakeId, itemId));
    }

    @Operation(summary = "Resolve a stocktake item problem", description = "Marks a stocktake item problem as resolved with the chosen resolution.")
    @ApiResponse(responseCode = "200", description = "Item updated and returned in the response body")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "No stocktake or item exists with the given IDs")
    @ApiResponse(responseCode = "409", description = "The item does not need a resolution, the resolution type is not valid for the current item status or the stocktake is already completed and cannot be modified")
    @PostMapping("/{itemId}/resolve")
    public ResponseEntity<FeltStocktakeItemDto> resolveStocktakeRollProblem(@PathVariable Long stocktakeId,
            @PathVariable Long itemId,
            @RequestBody @Valid ResolveFeltStocktakeProblemDto resolveFeltStocktakeProblemDto) {
        return ResponseEntity.ok(itemService.resolveProblem(stocktakeId, itemId, resolveFeltStocktakeProblemDto));
    }

    @Operation(summary = "Unresolve a stocktake item problem", description = "Clears the resolution for a stocktake item problem.")
    @ApiResponse(responseCode = "200", description = "Item updated and returned in the response body")
    @ApiResponse(responseCode = "404", description = "No stocktake or item exists with the given IDs")
    @ApiResponse(responseCode = "409", description = "Stocktake is already completed and cannot be modified")
    @PostMapping("/{itemId}/unresolve")
    public ResponseEntity<FeltStocktakeItemDto> unresolveStocktakeRollProblem(@PathVariable Long stocktakeId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.unresolveProblem(stocktakeId, itemId));
    }

}
