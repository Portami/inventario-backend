package ch.portami.inventorybackend.stocktake.felt.api;

import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.CreateFeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.ExtendStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.FeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.service.FeltStocktakeService;
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

    private final FeltStocktakeService stocktakeService;

    public FeltStocktakeController(FeltStocktakeService stocktakeService) {
        this.stocktakeService = stocktakeService;
    }

    @PostMapping
    public ResponseEntity<FeltStocktakeDto> createStocktake(
            @RequestBody @Valid CreateFeltStocktakeDto createFeltStocktakeDto) {
        return ResponseEntity.ok(stocktakeService.createStocktake(createFeltStocktakeDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeltStocktakeDto> getStocktakeById(@PathVariable Long id) {
        return ResponseEntity.ok(stocktakeService.getStocktake(id));
    }

    @GetMapping
    public ResponseEntity<List<FeltStocktakeDto>> getAllStocktakes() {
        return ResponseEntity.ok(stocktakeService.getAllStocktakes());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStocktakeById(@PathVariable Long id) {
        stocktakeService.deleteStocktake(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/extend")
    public ResponseEntity<FeltStocktakeDto> extendStocktake(@PathVariable Long id,
            @RequestBody @Valid ExtendStocktakeDto extendStocktakeDto) {
        return ResponseEntity.ok(stocktakeService.extendStocktake(id, extendStocktakeDto));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<FeltStocktakeDto> finalizeStocktake(@PathVariable Long id) {
        return ResponseEntity.ok(stocktakeService.completeStocktake(id));
    }

}
