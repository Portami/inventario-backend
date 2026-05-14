package ch.portami.inventorybackend.stocktaking.felt.api;

import ch.portami.inventorybackend.stocktaking.felt.dto.CreateFeltStocktakingScanDto;
import ch.portami.inventorybackend.stocktaking.felt.dto.FeltStocktakingScanDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/stocktakings/{stocktakingId}/scans")
public class FeltStocktakingScanController {

    @PostMapping
    public ResponseEntity<FeltStocktakingScanDto> createFeltStocktakingScan(@PathVariable Long stocktakingId,
            @RequestBody @Valid CreateFeltStocktakingScanDto createFeltStocktakingScanDto) {
        throw new RuntimeException("Not implemented yet");
    }

    @DeleteMapping("/{scanId}")
    public ResponseEntity<Void> deleteFeltStocktakingScan(@PathVariable Long stocktakingId, @PathVariable Long scanId) {
        throw new RuntimeException("Not implemented yet");
    }

}
