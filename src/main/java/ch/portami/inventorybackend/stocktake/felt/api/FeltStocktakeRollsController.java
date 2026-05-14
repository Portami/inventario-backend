package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.dto.ResolveFeltStocktakeProblemDto;
import ch.portami.inventorybackend.stocktake.felt.dto.StocktakeRollDto;
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
@RequestMapping("/api/stocktakes/{stocktakeId}/rolls")
public class FeltStocktakeRollsController {

    @GetMapping
    public ResponseEntity<List<StocktakeRollDto>> getStocktakeRolls(@PathVariable Long stocktakeId,
            @RequestParam(required = false) Long storageId) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/{rollId}")
    public ResponseEntity<StocktakeRollDto> getStocktakeRollById(@PathVariable Long stocktakeId,
            @PathVariable Long rollId) {
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/{rollId}/resolve")
    public ResponseEntity<StocktakeRollDto> resolveStocktakeProblem(@PathVariable Long stocktakeId,
            @PathVariable Long rollId,
            @RequestBody @Valid ResolveFeltStocktakeProblemDto resolveFeltStocktakeProblemDto) {
        throw new RuntimeException("Not implemented yet");
    }

}

