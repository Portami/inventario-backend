package ch.portami.inventorybackend.barcode.api;

import ch.portami.inventorybackend.barcode.BarcodeCode;
import ch.portami.inventorybackend.barcode.BarcodeService;
import ch.portami.inventorybackend.barcode.dto.BarcodeLookupDto;
import ch.portami.inventorybackend.barcode.validation.ValidBarcodeCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Barcodes")
@RestController
@RequestMapping("/api/barcodes")
@Validated
public class BarcodeController {

    private final BarcodeService service;

    public BarcodeController(BarcodeService service) {
        this.service = service;
    }

    @Operation(summary = "Resolve a scanned barcode", description = "Looks up the entity attached to a Datamatrix code.")
    @ApiResponse(responseCode = "200", description = "Barcode found — response indicates whether it belongs to a roll or a scrap piece and includes the entity ID.")
    @ApiResponse(responseCode = "400", description = "Code is not a valid positive integer.")
    @ApiResponse(responseCode = "404", description = "No barcode exists for the given code.")
    @GetMapping("/{code}")
    public ResponseEntity<BarcodeLookupDto> resolve(
            @PathVariable @Parameter(description = "Scanned barcode (positive integer).", example = "1")
            @ValidBarcodeCode BarcodeCode code) {
        return ResponseEntity.ok(service.findByCode(code.toId()));
    }
}
