package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.FeltService;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltDto;
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
@RequestMapping("/api/felts")
public class FeltController {

    private final FeltService feltService;

    public FeltController(FeltService feltService) {
        this.feltService = feltService;
    }

    @GetMapping
    public List<FeltDto> getAllFelts() {
        return feltService.getAllFelts();
    }

    @GetMapping("/{id}")
    public FeltDto getFelt(@PathVariable Long id) {
        return feltService.getFeltById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeltDto createFelt(@Valid @RequestBody CreateFeltDto request) {
        return feltService.createFelt(request);
    }

    @PutMapping("/{id}")
    public FeltDto updateFelt(
        @PathVariable Long id,
        @Valid @RequestBody UpdateFeltDto request
    ) {
        return feltService.updateFelt(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFelt(@PathVariable Long id) {
        feltService.deleteFelt(id);
    }
}
