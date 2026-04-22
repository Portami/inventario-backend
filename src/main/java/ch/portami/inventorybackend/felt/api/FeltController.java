package ch.portami.inventorybackend.felt.api;

import ch.portami.inventorybackend.felt.FeltService;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Felts", description = "Manage felt color variants. Each felt represents a unique combination of type, supplier, article number, variant specs (thickness, density, price) and color.")
@RestController
@RequestMapping("/api/felts")
public class FeltController {

    private final FeltService feltService;

    public FeltController(FeltService feltService) {
        this.feltService = feltService;
    }

    @Operation(summary = "List all felts")
    @ApiResponse(responseCode = "200", description = "List of felts (may be empty)")
    @GetMapping
    public ResponseEntity<List<FeltDto>> getAll() {
        return ResponseEntity.ok(feltService.findAll());
    }

    @Operation(summary = "Get a felt by ID")
    @ApiResponse(responseCode = "200", description = "Felt found")
    @ApiResponse(responseCode = "404", description = "No felt exists with the given ID")
    @GetMapping("/{id}")
    public ResponseEntity<FeltDto> getById(@Parameter(description = "Felt (color variant) ID") @PathVariable Long id) {
        return ResponseEntity.ok(feltService.findById(id));
    }

    @Operation(summary = "Create a felt", description = "Creates a new felt. Felts that share the same type, supplier, article number, and specs but differ only in color reuse the same underlying product entry.")
    @ApiResponse(responseCode = "201", description = "Felt created — Location header points to the new resource")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "FeltType or Supplier not found")
    @PostMapping
    public ResponseEntity<FeltDto> create(@RequestBody @Valid CreateFeltDto dto) {
        FeltDto created = feltService.create(dto);
        URI location = URI.create("/api/felts/" + created.id());
        return ResponseEntity.created(location)
                             .body(created);
    }

    @Operation(summary = "Partially update a felt", description = "Omit any field to leave it unchanged.")
    @ApiResponse(responseCode = "200", description = "Felt updated")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "Felt, FeltType, or Supplier not found")
    @PatchMapping("/{id}")
    public ResponseEntity<FeltDto> update(@Parameter(description = "Felt (color variant) ID") @PathVariable Long id,
            @RequestBody @Valid UpdateFeltDto dto) {
        return ResponseEntity.ok(feltService.update(id, dto));
    }

    @Operation(summary = "Delete a felt")
    @ApiResponse(responseCode = "204", description = "Felt deleted")
    @ApiResponse(responseCode = "404", description = "No felt exists with the given ID")
    @ApiResponse(responseCode = "409", description = "Felt still has rolls or scrap pieces attached")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Felt (color variant) ID") @PathVariable Long id) {
        feltService.delete(id);
        return ResponseEntity.noContent()
                             .build();
    }
}
