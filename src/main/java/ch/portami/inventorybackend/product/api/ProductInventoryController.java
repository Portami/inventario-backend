package ch.portami.inventorybackend.product.api;

import ch.portami.inventorybackend.product.dto.productinventory.ProductInventoryDto;
import ch.portami.inventorybackend.product.dto.productinventory.UpdateProductInventoryDto;
import ch.portami.inventorybackend.product.service.ProductInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product Inventory", description = "Manage product variant inventory levels.")
@RestController
@RequestMapping("/api/products/inventory")
public class ProductInventoryController {

    private final ProductInventoryService productInventoryService;

    public ProductInventoryController(ProductInventoryService productInventoryService) {
        this.productInventoryService = productInventoryService;
    }

    @Operation(summary = "Change inventory levels for product variants", description = "Adjusts the inventory levels for one or more product variants in specific storage locations. Each change specifies the product variant, storage location, and quantity change (positive to increase, negative to decrease). Returns the updated inventory levels after applying the changes. The operation is atomic: if any change fails (e.g. due to non-existent product variant or storage), no changes are applied.")
    @ApiResponse(responseCode = "200", description = "Inventory levels successfully updated. Response contains the new inventory levels for the affected product variants and storage locations in the same order as the input changes. Multiple entries for the same product variant and storage location will occur if they were included multiple times in the input list.")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "422", description = "One or more referenced product variants and/or storage locations do not exist")
    @PostMapping("/changes")
    public ResponseEntity<List<ProductInventoryDto>> changeInventory(
            @RequestBody @Valid List<@NotNull @Valid UpdateProductInventoryDto> inventoryChanges) {
        List<ProductInventoryDto> updatedInventory = productInventoryService.changeInventory(inventoryChanges);
        return ResponseEntity.ok(updatedInventory);
    }

}
