package ch.portami.inventorybackend.restocking.api;

import ch.portami.inventorybackend.restocking.RestockingService;
import ch.portami.inventorybackend.restocking.dto.SupplyDto;
import ch.portami.inventorybackend.restocking.dto.UpdateSupplyDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Restocking")
@RestController
@RequestMapping("/api/supply")
public class RestockingController {

    private final RestockingService service;

    public RestockingController(RestockingService service) {
        this.service = service;
    }

    @Operation(summary = "Update supply flags for a roll", description = "Omit a field to leave it unchanged.")
    @ApiResponse(responseCode = "200", description = "Supply flags updated")
    @ApiResponse(responseCode = "404", description = "No supply entry exists for the given roll ID")
    @PatchMapping("/{rollId}")
    public ResponseEntity<SupplyDto> update(
            @Parameter(description = "Felt roll ID") @PathVariable Long rollId,
            @RequestBody @Valid UpdateSupplyDto dto) {
        return ResponseEntity.ok(service.updateSupply(rollId, dto));
    }

    @Operation(summary = "List all open reorders", description = "Returns all rolls where either low-on-supply or reorder-in-process is true.")
    @ApiResponse(responseCode = "200", description = "List of open reorders")
    @GetMapping("/open-reorders")
    public ResponseEntity<List<SupplyDto>> openReorders() {
        return ResponseEntity.ok(service.findOpenReorders());
    }
}
