package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.FeltRollService;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollRequest;
import ch.portami.inventorybackend.felt.dto.FeltRollResponse;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollRequest;
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
@RequestMapping("/api/felt-rolls")
public class FeltRollController {

    private final FeltRollService feltService;

    public FeltRollController(FeltRollService feltService) {
        this.feltService = feltService;
    }

    @GetMapping
    public List<FeltRollResponse> getAllFeltRolls() {
        return feltService.getAllFeltRolls();
    }

    @GetMapping("/{id}")
    public FeltRollResponse getFeltRoll(@PathVariable Long id) {
        return feltService.getFeltRollById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeltRollResponse createFeltRoll(@Valid @RequestBody CreateFeltRollRequest request) {
        return feltService.createFeltRoll(request);
    }

    @PutMapping("/{id}")
    public FeltRollResponse updateFeltRoll(
        @PathVariable Long id,
        @Valid @RequestBody UpdateFeltRollRequest request
    ) {
        return feltService.updateFeltRoll(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFeltRoll(@PathVariable Long id) {
        feltService.deleteFeltRoll(id);
    }
}
