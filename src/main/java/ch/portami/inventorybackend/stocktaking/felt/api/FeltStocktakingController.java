package ch.portami.inventorybackend.stocktaking.felt.api;

import ch.portami.inventorybackend.stocktaking.felt.dto.CreateFeltStocktakingDto;
import ch.portami.inventorybackend.stocktaking.felt.dto.ExtendStocktakingDto;
import ch.portami.inventorybackend.stocktaking.felt.dto.FeltStocktakingDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocktakings")
public class FeltStocktakingController {

    @PostMapping
    public ResponseEntity<FeltStocktakingDto> createStocktaking(
            @RequestBody @Valid CreateFeltStocktakingDto createFeltStocktakingDto) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeltStocktakingDto> getStocktakingById(@PathVariable Long id) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping
    public ResponseEntity<List<FeltStocktakingDto>> getAllStocktakings() {
        throw new RuntimeException("Not implemented yet");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStocktakingById(@PathVariable Long id) {
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/{id}/extend")
    public ResponseEntity<FeltStocktakingDto> extendStocktaking(@PathVariable Long id,
            @RequestBody @Valid ExtendStocktakingDto extendStocktakingDto) {
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<FeltStocktakingDto> finalizeStocktaking(@PathVariable Long id) {
        throw new RuntimeException("Not implemented yet");
    }

}
