package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.dto.item.ResolveFeltStocktakeProblemDto;
import ch.portami.inventorybackend.stocktake.felt.dto.item.StocktakeItemDto;
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

    @GetMapping
    public ResponseEntity<List<StocktakeItemDto>> getStocktakeItems(@PathVariable Long stocktakeId,
            @RequestParam(required = false) @Nullable Long storageId) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<StocktakeItemDto> getStocktakeRollById(@PathVariable Long stocktakeId,
            @PathVariable Long itemId) {
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/{itemId}/resolve")
    public ResponseEntity<StocktakeItemDto> resolveStocktakeRollProblem(@PathVariable Long stocktakeId,
            @PathVariable Long itemId,
            @RequestBody @Valid ResolveFeltStocktakeProblemDto resolveFeltStocktakeProblemDto) {
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/{itemId}/unresolve")
    public ResponseEntity<StocktakeItemDto> unresolveStocktakeRollProblem(@PathVariable Long stocktakeId,
            @PathVariable Long itemId) {
        throw new RuntimeException("Not implemented yet");
    }

}

