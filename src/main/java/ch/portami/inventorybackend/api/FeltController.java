package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.FeltService;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltDto;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/felts")
public class FeltController {

    private final FeltService feltService;

    public FeltController(FeltService feltService) {
        this.feltService = feltService;
    }

    @GetMapping
    public ResponseEntity<List<FeltDto>> getAll() {
        return ResponseEntity.ok(feltService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeltDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(feltService.findById(id));
    }

    @PostMapping
    public ResponseEntity<FeltDto> create(@RequestBody @Valid CreateFeltDto dto) {
        FeltDto created = feltService.create(dto);
        URI location = URI.create("/api/felts/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeltDto> update(@PathVariable Long id, @RequestBody @Valid UpdateFeltDto dto) {
        return ResponseEntity.ok(feltService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feltService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
