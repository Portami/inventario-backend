package ch.portami.inventorybackend.cutassistant.api;

import ch.portami.inventorybackend.cutassistant.CuttingOptimizer;
import ch.portami.inventorybackend.cutassistant.domain.CutInput;
import ch.portami.inventorybackend.cutassistant.domain.CutResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for the cutting assistant, exposed under {@code /api/cut-assistant}.
 */
@RestController
@RequestMapping("/api/cut-assistant")
public class CutAssistantController {

    private final CuttingOptimizer optimizer;

    public CutAssistantController(CuttingOptimizer optimizer) {
        this.optimizer = optimizer;
    }

    @PostMapping("/optimize")
    public ResponseEntity<CutResult> optimize(@Valid @RequestBody CutInput input) {
        CutResult result = optimizer.optimize(input);
        return ResponseEntity.ok(result);
    }
}

