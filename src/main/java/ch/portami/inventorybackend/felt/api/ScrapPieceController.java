package ch.portami.inventorybackend.felt.api;

import ch.portami.inventorybackend.felt.ScrapPieceService;
import ch.portami.inventorybackend.felt.dto.CreateScrapPieceDto;
import ch.portami.inventorybackend.felt.dto.ScrapPieceDto;
import ch.portami.inventorybackend.felt.dto.UpdateScrapPieceDto;
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

@Tag(name = "Scrap Pieces")
@RestController
@RequestMapping("/api/scraps")
@Validated
public class ScrapPieceController {

    private final ScrapPieceService service;

    public ScrapPieceController(ScrapPieceService service) {
        this.service = service;
    }

    @Operation(summary = "List all scrap pieces")
    @ApiResponse(responseCode = "200", description = "List of scrap pieces (may be empty)")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ScrapPieceDto> getAll() {
        return service.findAll();
    }

    @Operation(summary = "Get a scrap piece by ID")
    @ApiResponse(responseCode = "200", description = "Scrap piece found")
    @ApiResponse(responseCode = "404", description = "No scrap piece exists with the given ID")
    @GetMapping("/{id}")
    public ResponseEntity<ScrapPieceDto> getById(@Parameter(description = "Scrap piece ID") @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Create a scrap piece", description = "Batch and storage are optional.")
    @ApiResponse(responseCode = "201", description = "Scrap piece created — Location header points to the new resource")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "Felt, batch, or storage not found")
    @PostMapping
    public ResponseEntity<ScrapPieceDto> create(@RequestBody @Valid CreateScrapPieceDto dto) {
        ScrapPieceDto created = service.create(dto);
        URI location = URI.create("/api/scraps/" + created.id());
        return ResponseEntity.created(location)
                             .body(created);
    }

    @Operation(summary = "Partially update a scrap piece", description = "Omit any field to leave it unchanged. Null batch or storage preserves the existing assignment.")
    @ApiResponse(responseCode = "200", description = "Scrap piece updated")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "Scrap piece, batch, or storage not found")
    @PatchMapping("/{id}")
    public ResponseEntity<ScrapPieceDto> update(@Parameter(description = "Scrap piece ID") @PathVariable Long id,
            @RequestBody @Valid UpdateScrapPieceDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Delete a scrap piece")
    @ApiResponse(responseCode = "204", description = "Scrap piece deleted")
    @ApiResponse(responseCode = "404", description = "No scrap piece exists with the given ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Scrap piece ID") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent()
                             .build();
    }
}
