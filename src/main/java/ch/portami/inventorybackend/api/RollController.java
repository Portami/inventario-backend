package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.FeltRollService;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rolls")
@RestController
@RequestMapping("/api/rolls")
public class RollController {

    private final FeltRollService service;

    public RollController(FeltRollService service) {
        this.service = service;
    }

    @Operation(
        summary = "Create a roll",
        description = """
            Creates a new felt roll and assigns it to the specified felt (color variant).
            Batch and storage are optional — supply their IDs to associate the roll with an
            incoming delivery batch or a physical shelf/location, or omit them to leave the roll unassigned.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Roll created — Location header points to the new resource"),
        @ApiResponse(responseCode = "400", description = "Validation error in the request body"),
        @ApiResponse(responseCode = "404", description = "Felt, batch, or storage not found")
    })
    @PostMapping
    public ResponseEntity<FeltRollDto> create(@RequestBody @Valid CreateFeltRollDto dto) {
        FeltRollDto created = service.create(dto);
        URI location = URI.create("/api/rolls/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @Operation(
        summary = "Get a roll by ID",
        description = "Returns a single roll with its full context: felt hierarchy details (color, article number, type, supplier, variant specs) plus batch and storage info."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Roll found"),
        @ApiResponse(responseCode = "404", description = "No roll exists with the given ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FeltRollDto> getById(
        @Parameter(description = "Roll ID") @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
        summary = "Update a roll",
        description = """
            Updates the dimensions and/or the batch and storage assignment of an existing roll.
            The felt a roll belongs to cannot be changed — delete and re-create the roll if reassignment is needed.
            Pass null for batchId or storageId to clear those associations.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Roll updated"),
        @ApiResponse(responseCode = "400", description = "Validation error in the request body"),
        @ApiResponse(responseCode = "404", description = "Roll, batch, or storage not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FeltRollDto> update(
        @Parameter(description = "Roll ID") @PathVariable Long id,
        @RequestBody @Valid UpdateFeltRollDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(
        summary = "Delete a roll",
        description = "Permanently deletes the roll. This action cannot be undone."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Roll deleted"),
        @ApiResponse(responseCode = "404", description = "No roll exists with the given ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @Parameter(description = "Roll ID") @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
