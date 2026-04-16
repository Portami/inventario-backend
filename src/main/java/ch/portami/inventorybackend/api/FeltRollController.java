package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.FeltRollService;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollDto;
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
@RequestMapping("/api/felts/{feltId}/variants/{variantId}/color-variants/{colorVariantId}/rolls")
public class FeltRollController {

    private final FeltRollService feltRollService;

    public FeltRollController(FeltRollService feltRollService) {
        this.feltRollService = feltRollService;
    }

    @GetMapping
    public List<FeltRollDto> getAllFeltRolls(
        @PathVariable Long feltId,
        @PathVariable Long variantId,
        @PathVariable Long colorVariantId
    ) {
        return feltRollService.getFeltRollsByColorVariant(feltId, variantId, colorVariantId);
    }

    @GetMapping("/{id}")
    public FeltRollDto getFeltRoll(
        @PathVariable Long feltId,
        @PathVariable Long variantId,
        @PathVariable Long colorVariantId,
        @PathVariable Long id
    ) {
        return feltRollService.getFeltRollById(id, feltId, variantId, colorVariantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeltRollDto createFeltRoll(
        @PathVariable Long feltId,
        @PathVariable Long variantId,
        @PathVariable Long colorVariantId,
        @Valid @RequestBody CreateFeltRollDto request
    ) {
        return feltRollService.createFeltRoll(feltId, variantId, colorVariantId, request);
    }

    @PutMapping("/{id}")
    public FeltRollDto updateFeltRoll(
        @PathVariable Long feltId,
        @PathVariable Long variantId,
        @PathVariable Long colorVariantId,
        @PathVariable Long id,
        @Valid @RequestBody UpdateFeltRollDto request
    ) {
        return feltRollService.updateFeltRoll(id, feltId, variantId, colorVariantId, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFeltRoll(
        @PathVariable Long feltId,
        @PathVariable Long variantId,
        @PathVariable Long colorVariantId,
        @PathVariable Long id
    ) {
        feltRollService.deleteFeltRoll(id, feltId, variantId, colorVariantId);
    }
}
