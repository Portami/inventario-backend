package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.FeltRollService;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rolls", description = "Manage felt rolls. Each roll belongs to exactly one felt (color variant) and tracks physical dimensions, batch, and storage location.")
@RestController
@RequestMapping("/api/felts/{feltId}/rolls")
public class FeltRollController {

    private final FeltRollService service;

    public FeltRollController(FeltRollService service) {
        this.service = service;
    }

    @Operation(summary = "List rolls for a felt")
    @ApiResponse(responseCode = "200", description = "List of rolls (may be empty)")
    @ApiResponse(responseCode = "404", description = "No felt exists with the given ID")
    @GetMapping
    public ResponseEntity<List<FeltRollDto>> getAll(
            @Parameter(description = "Felt (color variant) ID") @PathVariable Long feltId) {
        return ResponseEntity.ok(service.findAllByFelt(feltId));
    }
}
