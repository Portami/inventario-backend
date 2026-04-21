package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.BarcodeService;
import ch.portami.inventorybackend.felt.dto.BarcodeLookupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Barcodes")
@RestController
@RequestMapping("/api/barcodes")
public class BarcodeController {

    private final BarcodeService service;

    public BarcodeController(BarcodeService service) {
        this.service = service;
    }

    @Operation(summary = "Resolve a scanned barcode",
               description = "Looks up the entity attached to a Datamatrix code. The code is a numeric string, typically zero-padded (e.g. '00001'); padding is optional.")
    @ApiResponse(responseCode = "200", description = "Barcode found — response indicates whether it belongs to a roll or a scrap piece and includes the entity ID.")
    @ApiResponse(responseCode = "400", description = "Code is missing, non-numeric, or non-positive.")
    @ApiResponse(responseCode = "404", description = "No barcode exists for the given code.")
    @GetMapping("/{code}")
    public ResponseEntity<BarcodeLookupDto> resolve(
            @Parameter(description = "Scanned barcode (numeric, zero-padding optional).", example = "00001")
            @PathVariable String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }
}
