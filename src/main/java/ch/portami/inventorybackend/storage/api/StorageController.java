package ch.portami.inventorybackend.storage.api;

import ch.portami.inventorybackend.storage.dto.StorageDto;
import ch.portami.inventorybackend.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Storage", description = "Manage storage locations")
@RestController
@RequestMapping("/api/storages")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Operation(summary = "List all storage locations")
    @ApiResponse(responseCode = "200", description = "List of storage locations")
    @GetMapping
    public ResponseEntity<List<StorageDto>> listStorages() {
        return ResponseEntity.ok(storageService.findAll());
    }
}
