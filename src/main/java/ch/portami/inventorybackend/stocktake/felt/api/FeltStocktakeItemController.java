package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.dto.item.ResolveFeltStocktakeProblemDto;
import ch.portami.inventorybackend.stocktake.felt.dto.item.StocktakeItemDto;
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
            @RequestParam(required = false) Long storageId) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/roll/{rollId}")
    public ResponseEntity<StocktakeItemDto> getStocktakeRollById(@PathVariable Long stocktakeId,
            @PathVariable Long rollId) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/scrap/{scrapId}")
    public ResponseEntity<StocktakeItemDto> getStocktakeScrapById(@PathVariable Long stocktakeId,
            @PathVariable Long scrapId) {
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/roll/{rollId}/resolve")
    public ResponseEntity<StocktakeItemDto> resolveStocktakeRollProblem(@PathVariable Long stocktakeId,
            @PathVariable Long rollId,
            @RequestBody @Valid ResolveFeltStocktakeProblemDto resolveFeltStocktakeProblemDto) {
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/scrap/{scrapId}/resolve")
    public ResponseEntity<StocktakeItemDto> resolveStocktakeScrapProblem(@PathVariable Long stocktakeId,
            @PathVariable Long scrapId,
            @RequestBody @Valid ResolveFeltStocktakeProblemDto resolveFeltStocktakeProblemDto) {
        throw new RuntimeException("Not implemented yet");
    }

}

