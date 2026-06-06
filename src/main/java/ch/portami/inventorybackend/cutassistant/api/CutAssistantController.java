package ch.portami.inventorybackend.cutassistant.api;

import ch.portami.inventorybackend.cutassistant.CuttingOptimizer;
import ch.portami.inventorybackend.cutassistant.domain.CutInput;
import ch.portami.inventorybackend.cutassistant.domain.CutResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for the cutting assistant, exposed under {@code /api/cut-assistant}.
 */
@Tag(name = "Cut Assistant")
@RestController
@RequestMapping("/api/cut-assistant")
public class CutAssistantController {

    private final CuttingOptimizer optimizer;

    public CutAssistantController(CuttingOptimizer optimizer) {
        this.optimizer = optimizer;
    }

    @Operation(summary = "Optimize a set of cuts",
               description = "Computes how to place the requested pieces and reports assignments, total waste, and feasibility.")
    @ApiResponse(responseCode = "200", description = "Optimization ran; the result body reports whether all pieces fit.")
    @ApiResponse(responseCode = "400", description = "Request is invalid (e.g. no required pieces).")
    @PostMapping("/optimize")
    public ResponseEntity<CutResult> optimize(@Valid @RequestBody CutInput input) {
        CutResult result = optimizer.optimize(input);
        return ResponseEntity.ok(result);
    }
}

