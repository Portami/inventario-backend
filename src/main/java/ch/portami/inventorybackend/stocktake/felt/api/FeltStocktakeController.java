package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.CreateFeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.ExtendStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.FeltStocktakeDto;
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
@RequestMapping("/api/stocktakes")
public class FeltStocktakeController {

    @PostMapping
    public ResponseEntity<FeltStocktakeDto> createStocktake(
            @RequestBody @Valid CreateFeltStocktakeDto createFeltStocktakeDto) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeltStocktakeDto> getStocktakeById(@PathVariable Long id) {
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping
    public ResponseEntity<List<FeltStocktakeDto>> getAllStocktakes() {
        throw new RuntimeException("Not implemented yet");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStocktakeById(@PathVariable Long id) {
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/{id}/extend")
    public ResponseEntity<FeltStocktakeDto> extendStocktake(@PathVariable Long id,
            @RequestBody @Valid ExtendStocktakeDto extendStocktakeDto) {
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<FeltStocktakeDto> finalizeStocktake(@PathVariable Long id) {
        throw new RuntimeException("Not implemented yet");
    }

}

