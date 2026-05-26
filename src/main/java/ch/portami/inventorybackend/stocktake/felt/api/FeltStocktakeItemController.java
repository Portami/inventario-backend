package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemDto;
import ch.portami.inventorybackend.stocktake.felt.dto.item.ResolveFeltStocktakeProblemDto;
import ch.portami.inventorybackend.stocktake.felt.service.FeltStocktakeItemService;
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

@RestController
@RequestMapping("/api/stocktakes/{stocktakeId}/items")
public class FeltStocktakeItemController {

    private final FeltStocktakeItemService itemService;

    public FeltStocktakeItemController(FeltStocktakeItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<FeltStocktakeItemDto>> getStocktakeItems(@PathVariable Long stocktakeId,
            @RequestParam(required = false) @Nullable Long storageId) {
        return ResponseEntity.ok(itemService.getItems(stocktakeId, storageId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<FeltStocktakeItemDto> getStocktakeRollById(@PathVariable Long stocktakeId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItem(stocktakeId, itemId));
    }

    @PostMapping("/{itemId}/resolve")
    public ResponseEntity<FeltStocktakeItemDto> resolveStocktakeRollProblem(@PathVariable Long stocktakeId,
            @PathVariable Long itemId,
            @RequestBody @Valid ResolveFeltStocktakeProblemDto resolveFeltStocktakeProblemDto) {
        return ResponseEntity.ok(itemService.resolveProblem(stocktakeId, itemId, resolveFeltStocktakeProblemDto));
    }

    @PostMapping("/{itemId}/unresolve")
    public ResponseEntity<FeltStocktakeItemDto> unresolveStocktakeRollProblem(@PathVariable Long stocktakeId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.unresolveProblem(stocktakeId, itemId));
    }

}
