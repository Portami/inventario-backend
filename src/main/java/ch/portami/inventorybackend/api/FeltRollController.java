package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.felt.FeltRollService;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/felts/{feltId}/rolls")
public class FeltRollController {

    private final FeltRollService service;

    public FeltRollController(FeltRollService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<FeltRollDto>> getAll(@PathVariable Long feltId) {
        return ResponseEntity.ok(service.findAllByFelt(feltId));
    }
}
