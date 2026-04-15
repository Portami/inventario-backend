package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.FeltService;
import ch.portami.inventorybackend.felt.dto.CreateFeltColorVariantRequest;
import ch.portami.inventorybackend.felt.dto.FeltColorVariantResponse;
import ch.portami.inventorybackend.felt.dto.UpdateFeltColorVariantRequest;
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
@RequestMapping("/api/felt-color-variants")
public class FeltColorVariantController {

    private final FeltService feltService;

    public FeltColorVariantController(FeltService feltService) {
        this.feltService = feltService;
    }

    @GetMapping
    public List<FeltColorVariantResponse> getAllFeltColorVariants() {
        return feltService.getAllFeltColorVariants();
    }

    @GetMapping("/{id}")
    public FeltColorVariantResponse getFeltColorVariant(@PathVariable Long id) {
        return feltService.getFeltColorVariantById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeltColorVariantResponse createFeltColorVariant(
        @Valid @RequestBody CreateFeltColorVariantRequest request
    ) {
        return feltService.createFeltColorVariant(request);
    }

    @PutMapping("/{id}")
    public FeltColorVariantResponse updateFeltColorVariant(
        @PathVariable Long id,
        @Valid @RequestBody UpdateFeltColorVariantRequest request
    ) {
        return feltService.updateFeltColorVariant(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFeltColorVariant(@PathVariable Long id) {
        feltService.deleteFeltColorVariant(id);
    }
}
