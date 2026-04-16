package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.FeltService;
import ch.portami.inventorybackend.felt.dto.CreateFeltColorVariantDto;
import ch.portami.inventorybackend.felt.dto.FeltColorVariantDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltColorVariantDto;
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
@RequestMapping("/api/felts/{feltId}/variants/{variantId}/color-variants")
public class FeltColorVariantController {

    private final FeltService feltService;

    public FeltColorVariantController(FeltService feltService) {
        this.feltService = feltService;
    }

    @GetMapping
    public List<FeltColorVariantDto> getAllFeltColorVariants(
        @PathVariable Long feltId,
        @PathVariable Long variantId
    ) {
        return feltService.getFeltColorVariantsByVariant(feltId, variantId);
    }

    @GetMapping("/{id}")
    public FeltColorVariantDto getFeltColorVariant(
        @PathVariable Long feltId,
        @PathVariable Long variantId,
        @PathVariable Long id
    ) {
        return feltService.getFeltColorVariantById(id, feltId, variantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeltColorVariantDto createFeltColorVariant(
        @PathVariable Long feltId,
        @PathVariable Long variantId,
        @Valid @RequestBody CreateFeltColorVariantDto request
    ) {
        return feltService.createFeltColorVariant(feltId, variantId, request);
    }

    @PutMapping("/{id}")
    public FeltColorVariantDto updateFeltColorVariant(
        @PathVariable Long feltId,
        @PathVariable Long variantId,
        @PathVariable Long id,
        @Valid @RequestBody UpdateFeltColorVariantDto request
    ) {
        return feltService.updateFeltColorVariant(id, feltId, variantId, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFeltColorVariant(
        @PathVariable Long feltId,
        @PathVariable Long variantId,
        @PathVariable Long id
    ) {
        feltService.deleteFeltColorVariant(id, feltId, variantId);
    }
}
