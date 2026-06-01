package ch.portami.inventorybackend.felt.api;

import ch.portami.inventorybackend.felt.FeltRollService;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.dto.SplitFeltRollDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rolls")
@RestController
@RequestMapping("/api/rolls")
@Validated
public class RollController {

    private final FeltRollService service;

    public RollController(FeltRollService service) {
        this.service = service;
    }

    @Operation(summary = "List all rolls")
    @ApiResponse(responseCode = "200", description = "List of rolls (may be empty)")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<FeltRollDto> getAll() {
        return service.findAll();
    }

    @Operation(summary = "Get a roll by ID")
    @ApiResponse(responseCode = "200", description = "Roll found")
    @ApiResponse(responseCode = "404", description = "No roll exists with the given ID")
    @GetMapping("/{id}")
    public ResponseEntity<FeltRollDto> getById(@Parameter(description = "Roll ID") @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Create a roll", description = "Batch and storage are optional.")
    @ApiResponse(responseCode = "201", description = "Roll created — Location header points to the new resource")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "Felt, batch, or storage not found")
    @PostMapping
    public ResponseEntity<FeltRollDto> create(@RequestBody @Valid CreateFeltRollDto dto) {
        FeltRollDto created = service.create(dto);
        URI location = URI.create("/api/rolls/" + created.id());
        return ResponseEntity.created(location)
                             .body(created);
    }

    @Operation(summary = "Partially update a roll", description = "Omit any field to leave it unchanged. Null batch or storage preserves the existing assignment.")
    @ApiResponse(responseCode = "200", description = "Roll updated")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "Roll, batch, or storage not found")
    @PatchMapping("/{id}")
    public ResponseEntity<FeltRollDto> update(@Parameter(description = "Roll ID") @PathVariable Long id,
            @RequestBody @Valid UpdateFeltRollDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Split a roll", description = "Creates a new roll by cross-cutting the source roll. The new roll's length equals the source roll's width. The source roll's length is reduced by the specified width.")
    @ApiResponse(responseCode = "201", description = "New roll created from split — Location header points to the new resource")
    @ApiResponse(responseCode = "400", description = "Validation error (width must be positive)")
    @ApiResponse(responseCode = "404", description = "Source roll not found")
    @ApiResponse(responseCode = "409", description = "Width exceeds source roll length")
    @PostMapping("/{id}/split")
    public ResponseEntity<FeltRollDto> split(@Parameter(description = "Source Roll ID") @PathVariable Long id,
            @RequestBody @Valid SplitFeltRollDto dto) {
        FeltRollDto created = service.split(id, dto);
        URI location = URI.create("/api/rolls/" + created.id());
        return ResponseEntity.created(location)
                             .body(created);
    }

    @Operation(summary = "Delete a roll")
    @ApiResponse(responseCode = "204", description = "Roll deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Roll ID") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent()
                             .build();
    }
}
