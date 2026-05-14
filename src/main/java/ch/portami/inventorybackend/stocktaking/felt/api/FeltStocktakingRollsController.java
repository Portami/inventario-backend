package ch.portami.inventorybackend.stocktaking.felt.api;

import ch.portami.inventorybackend.stocktaking.felt.dto.ResolveFeltStocktakingProblemDto;
import ch.portami.inventorybackend.stocktaking.felt.dto.StocktakingRollDto;
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
@RequestMapping("/api/stocktakings/{stocktakingId}/rolls")
public class FeltStocktakingRollsController {

    @GetMapping
    public ResponseEntity<List<StocktakingRollDto>> getStocktakingRolls(@PathVariable Long stocktakingId,
            @RequestParam(required = false) Long storageId) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/{rollId}")
    public ResponseEntity<StocktakingRollDto> getStocktakingRollById(@PathVariable Long stocktakingId,
            @PathVariable Long rollId) {
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/{rollId}/resolve")
    public ResponseEntity<StocktakingRollDto> resolveStocktakingProblem(@PathVariable Long stocktakingId,
            @PathVariable Long rollId,
            @RequestBody @Valid ResolveFeltStocktakingProblemDto resolveFeltStocktakingProblemDto) {
        throw new RuntimeException("Not implemented yet");
    }

}
