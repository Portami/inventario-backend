package ch.portami.inventorybackend.felt.api;

import ch.portami.inventorybackend.felt.FeltRollService;
import ch.portami.inventorybackend.felt.FeltService;
import ch.portami.inventorybackend.felt.ScrapPieceService;
import ch.portami.inventorybackend.felt.dto.BatchDto;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.dto.ScrapPieceDto;
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

@Tag(name = "Felts", description = "Manage felts. Each felt represents a unique combination of type, supplier, article number, thickness, density, price and color.")
@RestController
@RequestMapping("/api/felts")
public class FeltController {

    private final FeltService feltService;
    private final FeltRollService feltRollService;
    private final ScrapPieceService scrapPieceService;

    public FeltController(FeltService feltService, FeltRollService feltRollService,
            ScrapPieceService scrapPieceService) {
        this.feltService = feltService;
        this.feltRollService = feltRollService;
        this.scrapPieceService = scrapPieceService;
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
    public ResponseEntity<FeltDto> getById(@Parameter(description = "Felt ID") @PathVariable Long id) {
        return ResponseEntity.ok(feltService.findById(id));
    }

    @Operation(summary = "Create a felt", description = "Creates a new felt.")
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
    public ResponseEntity<FeltDto> update(@Parameter(description = "Felt ID") @PathVariable Long id,
            @RequestBody @Valid UpdateFeltDto dto) {
        return ResponseEntity.ok(feltService.update(id, dto));
    }

    @Operation(summary = "Delete a felt")
    @ApiResponse(responseCode = "204", description = "Felt deleted")
    @ApiResponse(responseCode = "404", description = "No felt exists with the given ID")
    @ApiResponse(responseCode = "409", description = "Felt still has rolls or scrap pieces attached")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Felt ID") @PathVariable Long id) {
        feltService.delete(id);
        return ResponseEntity.noContent()
                             .build();
    }

    @Operation(summary = "List rolls for a felt")
    @ApiResponse(responseCode = "200", description = "List of rolls (may be empty)")
    @ApiResponse(responseCode = "404", description = "No felt exists with the given ID")
    @GetMapping("/{feltId}/rolls")
    public ResponseEntity<List<FeltRollDto>> getAll(@Parameter(description = "Felt ID") @PathVariable Long feltId) {
        return ResponseEntity.ok(feltRollService.findAllByFelt(feltId));
    }

    @Operation(summary = "List scrap pieces for a felt")
    @ApiResponse(responseCode = "200", description = "List of scrap pieces (may be empty)")
    @ApiResponse(responseCode = "404", description = "No felt exists with the given ID")
    @GetMapping("/{feltId}/scraps")
    public ResponseEntity<List<ScrapPieceDto>> getScraps(
            @Parameter(description = "Felt ID") @PathVariable Long feltId) {
        return ResponseEntity.ok(scrapPieceService.findAllByFelt(feltId));
    }

    @Operation(summary = "List batches for a felt")
    @ApiResponse(responseCode = "200", description = "List of batches")
    @GetMapping("/{feltId}/batches")
    public List<BatchDto> getBatches(@Parameter(description = "Felt ID") @PathVariable Long feltId) {
        return feltRollService.findAllBatchesByFelt(feltId);
    }
}