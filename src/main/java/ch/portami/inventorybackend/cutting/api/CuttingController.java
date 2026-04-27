package ch.portami.inventorybackend.cutting.api;

import ch.portami.inventorybackend.cutting.CuttingOptimizer;
import ch.portami.inventorybackend.cutting.domain.CutInput;
import ch.portami.inventorybackend.cutting.domain.CutResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cut-assistant")
public class CuttingController {

    private final CuttingOptimizer optimizer;

    public CuttingController(CuttingOptimizer optimizer) {
        this.optimizer = optimizer;
    }

    @PostMapping("/optimize")
    public ResponseEntity<CutResult> optimize(@RequestBody CutInput input) {
        CutResult result = optimizer.optimize(input);
        return ResponseEntity.ok(result);
    }
}

