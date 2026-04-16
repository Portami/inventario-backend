package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.FeltService;
import ch.portami.inventorybackend.felt.dto.CreateFeltTypeDto;
import ch.portami.inventorybackend.felt.dto.FeltTypeDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltTypeDto;
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
@RequestMapping("/api/felt-types")
public class FeltTypeController {

    private final FeltService feltService;

    public FeltTypeController(FeltService feltService) {
        this.feltService = feltService;
    }

    @GetMapping
    public List<FeltTypeDto> getAllFeltTypes() {
        return feltService.getAllFeltTypes();
    }

    @GetMapping("/{id}")
    public FeltTypeDto getFeltType(@PathVariable Long id) {
        return feltService.getFeltTypeById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeltTypeDto createFeltType(@Valid @RequestBody CreateFeltTypeDto request) {
        return feltService.createFeltType(request);
    }

    @PutMapping("/{id}")
    public FeltTypeDto updateFeltType(
        @PathVariable Long id,
        @Valid @RequestBody UpdateFeltTypeDto request
    ) {
        return feltService.updateFeltType(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFeltType(@PathVariable Long id) {
        feltService.deleteFeltType(id);
    }
}
