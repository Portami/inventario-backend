package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.FeltService;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
        summary = "List all felts",
        description = "Returns all felt color variants with their complete product hierarchy flattened into a single object."
    )
    @ApiResponse(responseCode = "200", description = "List of felts (may be empty)")
    @GetMapping
    public ResponseEntity<List<FeltDto>> getAll() {
        return ResponseEntity.ok(feltService.findAll());
    }

    @Operation(
        summary = "Get a felt by ID",
        description = "Returns a single felt color variant by its ID. The ID is the FeltColorVariant identifier returned on creation."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Felt found"),
        @ApiResponse(responseCode = "404", description = "No felt exists with the given ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FeltDto> getById(
        @Parameter(description = "Felt (color variant) ID") @PathVariable Long id
    ) {
        return ResponseEntity.ok(feltService.findById(id));
    }

    @Operation(
        summary = "Create a felt",
        description = """
            Creates a new felt color variant using a find-or-create cascade:
            - FeltType is looked up by ID; a 404 is returned if it does not exist.
            - Supplier is looked up by ID; a 404 is returned if it does not exist.
            - Felt is looked up by (feltType, supplier, articleNumber); created if not found.
            - FeltVariant is looked up by (felt, thickness, density, price); created if not found.
            - FeltColorVariant is always created as a new record.

            This means creating two felts with the same article number and specs but different colors
            will reuse the same underlying Felt and FeltVariant.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Felt created — Location header points to the new resource"),
        @ApiResponse(responseCode = "400", description = "Validation error in the request body"),
        @ApiResponse(responseCode = "404", description = "FeltType or Supplier not found")
    })
    @PostMapping
    public ResponseEntity<FeltDto> create(@RequestBody @Valid CreateFeltDto dto) {
        FeltDto created = feltService.create(dto);
        URI location = URI.create("/api/felts/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @Operation(
        summary = "Partially update a felt",
        description = """
            Partially updates an existing felt color variant. Every field is optional — omit any \
            field (or send it as null) to leave it unchanged.

            The update logic is hierarchy-aware:
            - color and supplierColor are updated directly on the color variant.
            - If feltType, supplier, or articleNumber change, a matching Felt is found or created.
              The service checks whether the underlying Felt and FeltVariant are shared before \
              deciding whether to mutate them in-place or re-point this color variant to a \
              different entity.
            - If thickness, density, or price change:
                - When the FeltVariant is shared by other color variants, a matching variant is \
                  found or created and this color variant is re-pointed to it.
                - When the FeltVariant is exclusive to this color variant, it is mutated in-place.

            Sibling color variants (those sharing the same underlying Felt or FeltVariant) are \
            never affected by an update to a single color variant.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Felt updated"),
        @ApiResponse(responseCode = "400", description = "Validation error in the request body (e.g. non-positive thickness)"),
        @ApiResponse(responseCode = "404", description = "Felt, FeltType, or Supplier not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<FeltDto> update(
        @Parameter(description = "Felt (color variant) ID") @PathVariable Long id,
        @RequestBody @Valid UpdateFeltDto dto
    ) {
        return ResponseEntity.ok(feltService.update(id, dto));
    }

    @Operation(
        summary = "Delete a felt",
        description = """
            Deletes the felt color variant. Does not cascade to the underlying FeltVariant, Felt,
            or FeltType — those are shared resources and remain if still referenced by other color variants.
            Any rolls that belong to this felt must be deleted first; otherwise a 409 is returned.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Felt deleted"),
        @ApiResponse(responseCode = "404", description = "No felt exists with the given ID"),
        @ApiResponse(responseCode = "409", description = "Felt still has rolls or scrap pieces attached")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @Parameter(description = "Felt (color variant) ID") @PathVariable Long id
    ) {
        feltService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
