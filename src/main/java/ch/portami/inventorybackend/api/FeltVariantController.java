package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.FeltService;
import ch.portami.inventorybackend.felt.dto.CreateFeltVariantDto;
import ch.portami.inventorybackend.felt.dto.FeltVariantDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltVariantDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/felt-variants")
public class FeltVariantController {

    private final FeltService feltService;

    public FeltVariantController(FeltService feltService) {
        this.feltService = feltService;
    }

    @GetMapping
    public List<FeltVariantDto> getAllFeltVariants() {
        return feltService.getAllFeltVariants();
    }

    @GetMapping("/{id}")
    public FeltVariantDto getFeltVariant(@PathVariable Long id) {
        return feltService.getFeltVariantById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeltVariantDto createFeltVariant(@Valid @RequestBody CreateFeltVariantDto request) {
        return feltService.createFeltVariant(request);
    }

    @PutMapping("/{id}")
    public FeltVariantDto updateFeltVariant(
        @PathVariable Long id,
        @Valid @RequestBody UpdateFeltVariantDto request
    ) {
        return feltService.updateFeltVariant(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFeltVariant(@PathVariable Long id) {
        feltService.deleteFeltVariant(id);
    }
}
